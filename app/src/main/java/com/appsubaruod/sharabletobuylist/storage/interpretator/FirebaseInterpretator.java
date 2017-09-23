package com.appsubaruod.sharabletobuylist.storage.interpretator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.models.Item;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.firebase.FirebaseItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by s-yamada on 2017/09/18.
 */

public class FirebaseInterpretator implements StorageInterpretator {
    private static final String LOG_TAG = FirebaseInterpretator.class.getName();
    public static final String ITEM_OBJECT = "itemObject";
    public static final String UNIQUE_KEY_MAP = "uniqueKeyMap";

    private final FirebaseDatabase mFirebaseDatabase;
    private Set<StorageEvent> mEventListeners = new CopyOnWriteArraySet<>();
    private List<Item> mItemList = new ArrayList<>();
    private Map<String, String> mKeyMap = new HashMap<>();
    private CountDownLatch mItemObtainedLatch = new CountDownLatch(1);

    public FirebaseInterpretator(Context context) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        // do not store context
        startMonitoringItemChange();
    }

    private void startMonitoringItemChange() {
        DatabaseReference reference = getRootReference();
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "ValueEventListener.onDataChanged");
                HashMap<String, HashMap<String, Object>> map = ((HashMap) dataSnapshot.getValue());

                HashMap<String, HashMap<String, String>> itemMap;
                HashMap<String, String> keyMap;

                if (map == null) {
                    itemMap = new HashMap<>();
                    keyMap = new HashMap<>();
                } else {
                    itemMap = (HashMap) map.get(ITEM_OBJECT);
                    keyMap = (HashMap) map.get(UNIQUE_KEY_MAP);
                }

                CompletableFuture<Void> itemListUpdateFuture =
                        CompletableFuture.supplyAsync(() -> itemMap)
                        .thenAcceptAsync(itemUpdate);

                CompletableFuture<Void> keyUpdateFuture =
                        CompletableFuture.supplyAsync(() -> keyMap)
                        .thenAcceptAsync(keyUpdate);

                itemListUpdateFuture.runAfterBoth(keyUpdateFuture, () -> {
                    // count down to invoke waiting thread.
                    // note couldDown() do nothing if could is already 0.
                    mItemObtainedLatch.countDown();
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private Consumer<HashMap<String, HashMap<String, String>>> itemUpdate = (itemMap -> {
                List<Item> itemList = new CopyOnWriteArrayList<>();

                itemMap.forEach((uniqueId, itemValueMap) -> {
                    final String[] itemName = new String[1];
                    final String[] isBought = new String[1];
                    itemValueMap.forEach((key, value) -> {
                        Log.d(LOG_TAG, key + ", " + value);
                        if ("itemName".equals(key)) {
                            itemName[0] = value;
                        } else {
                            isBought[0] = value;
                        }
                    });
                    itemList.add(0, new Item(itemName[0], isBought[0]));
                });

                // onDataChanged is also called when firstly ValueEventListener is added
                // It should be handled as initialization and does not notify to the listener
                if (mItemObtainedLatch.getCount() == 0) {
                    notifyItemDeleted(mItemList, itemList);
                    notifyItemAdded(mItemList, itemList);
                    notifyItemCompleted(mItemList, itemList);
                }

                mItemList = itemList;
            });

            private Consumer<HashMap<String, String>> keyUpdate = (keyMap -> {
                mKeyMap = keyMap;
            });
        });
    }

    private void notifyItemCompleted(@NonNull List<Item> oldItemList, @NonNull List<Item> newItemList) {
        // collect remaining items and check if status changed
        // this operation supposes that item of same name is not contained in the list(like Set obj)
        List<String> oldItemNameList = getNameList(oldItemList);
        List<String> newItemNameList = getNameList(newItemList);

        oldItemNameList.retainAll(newItemNameList);

        oldItemNameList.forEach(itemName -> {
            Optional<Item> oldItem = oldItemList.stream().filter(oldOneItem -> oldOneItem.getItemName().equals(itemName)).findFirst();
            Optional<Item> newItem = newItemList.stream().filter(newOneItem -> newOneItem.getItemName().equals(itemName)).findFirst();

            oldItem.ifPresent(oldExistingItem -> {
                newItem.ifPresent(newExistingItem -> {
                    if (oldExistingItem.isBought() != newExistingItem.isBought()) {
                        mEventListeners.forEach(listener -> {
                            listener.onItemCompleted(oldExistingItem.getItemName(), newExistingItem.isBought());
                        });
                    }
                });
            });
        });
    }

    private void notifyItemAdded(@NonNull List<Item> oldItemList, @NonNull List<Item> newItemList) {
        List<String> oldItemNameList = getNameList(oldItemList);
        List<String> newItemNameList = getNameList(newItemList);

        newItemNameList.removeAll(oldItemNameList);

        mEventListeners.forEach(listener -> {
            newItemNameList.forEach(itemName -> listener.onItemAdded(itemName));
        });
    }

    private void notifyItemDeleted(@NonNull List<Item> oldItemList, @NonNull List<Item> newItemList) {
        List<String> oldItemNameList = getNameList(oldItemList);
        List<String> newItemNameList = getNameList(newItemList);

        oldItemNameList.removeAll(newItemNameList);

        mEventListeners.forEach(listener -> {
            oldItemNameList.forEach(itemName -> listener.onItemDeleted(itemName));
        });
    }

    private List<String> getNameList(@NonNull List<Item> itemList) {
        List<String> nameList =
                itemList.stream().map(item -> item.getItemName()).collect(Collectors.toList());
        return nameList;
    }

    @Override
    public void add(String itemToAdd) {
        // TODO quick add operation can add same value.
        // Also it can occur on off line mode and multi update from other user.
        if (mKeyMap.containsKey(itemToAdd)) {
            setCompleted(itemToAdd, false);
            return;
        }

        DatabaseReference rootReference = getRootReference();
        DatabaseReference dataReference = getDataReference();

        String key = dataReference.push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        String itemObjectPath =
                (new StringBuilder())
                        .append("/")
                        .append(ITEM_OBJECT)
                        .append("/")
                        .toString();
        String uniqueKeyMapPath =
                (new StringBuilder())
                        .append("/")
                        .append(UNIQUE_KEY_MAP)
                        .append("/")
                        .toString();
        childUpdates.put(itemObjectPath + key + "/bought", "false");
        childUpdates.put(itemObjectPath + key + "/itemName", itemToAdd);
        childUpdates.put(uniqueKeyMapPath + itemToAdd, key);

        rootReference.updateChildren(childUpdates);
    }

    private DatabaseReference getRootReference() {
        return mFirebaseDatabase.getReference();
    }

    private DatabaseReference getDataReference() {
        return mFirebaseDatabase.getReference(ITEM_OBJECT);
    }

    private DatabaseReference getKeyMapReference() {
        return mFirebaseDatabase.getReference(UNIQUE_KEY_MAP);
    }

    @Override
    public void removeItem(String itemToDelete) {
        if (!mKeyMap.containsKey(itemToDelete)) {
            return;
        }

        String key = mKeyMap.get(itemToDelete);
        DatabaseReference rootReference = getRootReference();

        Map<String, Object> childRemoval = new HashMap<>();
        String itemObjectPath =
                (new StringBuilder())
                        .append("/")
                        .append(ITEM_OBJECT)
                        .append("/")
                        .toString();
        String uniqueKeyMapPath =
                (new StringBuilder())
                        .append("/")
                        .append(UNIQUE_KEY_MAP)
                        .append("/")
                        .toString();
        childRemoval.put(itemObjectPath + key + "/bought", null);
        childRemoval.put(itemObjectPath + key + "/itemName", null);
        childRemoval.put(uniqueKeyMapPath + itemToDelete, null);

        rootReference.updateChildren(childRemoval);
    }

    @Override
    public void removeAllItems() {
        getRootReference().removeValue();
    }

    @Override
    public List<Item> getAllItems() {
        try {
            if (!mItemObtainedLatch.await(10, TimeUnit.SECONDS)) {
                Log.w(LOG_TAG, "Latch does not released. TIMEOUT!!");
            }
        } catch (InterruptedException e) {
            Log.w(LOG_TAG, e.getMessage());
        }
        return mItemList;
    }

    @Override
    public void setCompleted(String itemToSetCompleted, boolean isCompleted) {
        if (!mKeyMap.containsKey(itemToSetCompleted)) {
            return;
        }
        if (mKeyMap.get(itemToSetCompleted).equals(isCompleted)) {
            return;
        }

        DatabaseReference dataReference = getDataReference();
        dataReference.child(mKeyMap.get(itemToSetCompleted))
                .setValue(new FirebaseItem(itemToSetCompleted, isCompleted));
    }

    @Override
    public void registerStorageEventListener(StorageEvent listener) {
        mEventListeners.add(listener);
    }
}
