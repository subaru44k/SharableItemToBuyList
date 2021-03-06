package com.appsubaruod.sharabletobuylist.storage.interpretator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.models.Item;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.firebase.FirebaseItem;
import com.appsubaruod.sharabletobuylist.util.Constant;
import com.appsubaruod.sharabletobuylist.util.FirebasePersistentDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private final Object mLock = new Object();
    private static final String LOG_TAG = FirebaseInterpretator.class.getName();
    private static final String ITEM_OBJECT_PATH = "itemObject";
    private static final String UNIQUE_KEY_PATH = "uniqueKeyMap";

    private String mRootPath = "";
    private String mItemObjectPath = ITEM_OBJECT_PATH;
    private String mUniqueKeyPath = UNIQUE_KEY_PATH;

    private final FirebaseDatabase mFirebaseDatabase;
    private Set<StorageEvent> mEventListeners = new CopyOnWriteArraySet<>();
    private List<Item> mItemList;
    private Map<String, String> mKeyMap;
    private CountDownLatch mItemObtainedLatch;
    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            synchronized (mLock) {
                Log.d(LOG_TAG, "ValueEventListener.onDataChanged");
                HashMap<String, HashMap<String, Object>> map = ((HashMap) dataSnapshot.getValue());

                HashMap<String, HashMap<String, String>> itemMap;
                HashMap<String, String> keyMap;

                if (map == null) {
                    itemMap = new HashMap<>();
                    keyMap = new HashMap<>();
                } else {
                    itemMap = (HashMap) map.get(ITEM_OBJECT_PATH);
                    if (itemMap == null) {
                        itemMap = new HashMap<>();
                    }
                    keyMap = (HashMap) map.get(UNIQUE_KEY_PATH);
                    if (keyMap == null) {
                        keyMap = new HashMap<>();
                    }
                }

                HashMap<String, HashMap<String, String>> finalItemMap = itemMap;
                CompletableFuture<Void> itemListUpdateFuture =
                        CompletableFuture.supplyAsync(() -> finalItemMap)
                                .thenAcceptAsync(itemUpdate);

                HashMap<String, String> finalKeyMap = keyMap;
                CompletableFuture<Void> keyUpdateFuture =
                        CompletableFuture.supplyAsync(() -> finalKeyMap)
                                .thenAcceptAsync(keyUpdate);

                itemListUpdateFuture.runAfterBoth(keyUpdateFuture, () -> {
                    // count down to invoke waiting thread.
                    // note couldDown() do nothing if could is already 0.
                    mItemObtainedLatch.countDown();
                });
            }
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
    };

    public FirebaseInterpretator(Context context, @Nullable String rootPath) {
        mFirebaseDatabase = FirebasePersistentDatabase.getInstance();

        if (rootPath == null || "".equals(rootPath)) {
            changeDatabasePath(Constant.DEFAULT_PATH);
        } else {
            changeDatabasePath(rootPath);
        }

        // do not store context
        startMonitoringItemChange();
    }

    public void changeRootPath(String rootPath) {
        stopMonitoringItemChange();
        changeDatabasePath(rootPath);
        startMonitoringItemChange();
    }

    @Override
    public String createAndGetNewUniqueId() {
        return getSharableChannelReference().push().getKey();
    }

    private void changeDatabasePath(String rootPath) {
        mRootPath = rootPath;
        mItemObjectPath = rootPath + "/" + ITEM_OBJECT_PATH;
        mUniqueKeyPath = rootPath + "/" + UNIQUE_KEY_PATH;
        initializeInternalValues();
    }

    private void initializeInternalValues() {
        mItemList = new ArrayList<>();
        mKeyMap = new HashMap<>();
        mItemObtainedLatch = new CountDownLatch(1);
    }

    private void startMonitoringItemChange() {
        DatabaseReference reference = getRootReference();
        reference.addValueEventListener(mValueEventListener);
    }

    private void stopMonitoringItemChange() {
        DatabaseReference reference = getRootReference();
        reference.removeEventListener(mValueEventListener);
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
        Log.d(LOG_TAG, "add item : " + itemToAdd);
        synchronized (mLock) {
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
                            .append(ITEM_OBJECT_PATH)
                            .append("/")
                            .toString();
            String uniqueKeyMapPath =
                    (new StringBuilder())
                            .append("/")
                            .append(UNIQUE_KEY_PATH)
                            .append("/")
                            .toString();
            childUpdates.put(itemObjectPath + key + "/bought", "false");
            childUpdates.put(itemObjectPath + key + "/itemName", itemToAdd);
            childUpdates.put(uniqueKeyMapPath + itemToAdd, key);

            rootReference.updateChildren(childUpdates);
        }
    }

    private DatabaseReference getSharableChannelReference() {
        return mFirebaseDatabase.getReference(Constant.SHARABLE_CHANNEL_ROOT_PATH);
    }

    private DatabaseReference getRootReference() {
        return mFirebaseDatabase.getReference(mRootPath);
    }

    private DatabaseReference getDataReference() {
        return mFirebaseDatabase.getReference(mItemObjectPath);
    }

    private DatabaseReference getKeyMapReference() {
        return mFirebaseDatabase.getReference(mUniqueKeyPath);
    }

    @Override
    public void removeItem(String itemToDelete) {
        synchronized (mLock) {
            if (!mKeyMap.containsKey(itemToDelete)) {
                return;
            }

            String key = mKeyMap.get(itemToDelete);
            DatabaseReference rootReference = getRootReference();

            Map<String, Object> childRemoval = new HashMap<>();
            String itemObjectPath =
                    (new StringBuilder())
                            .append("/")
                            .append(ITEM_OBJECT_PATH)
                            .append("/")
                            .toString();
            String uniqueKeyMapPath =
                    (new StringBuilder())
                            .append("/")
                            .append(UNIQUE_KEY_PATH)
                            .append("/")
                            .toString();
            childRemoval.put(itemObjectPath + key + "/bought", null);
            childRemoval.put(itemObjectPath + key + "/itemName", null);
            childRemoval.put(uniqueKeyMapPath + itemToDelete, null);

            rootReference.updateChildren(childRemoval);
        }
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
        Log.d(LOG_TAG, "setCompleted : " + isCompleted);
        synchronized (mLock) {
            if (!mKeyMap.containsKey(itemToSetCompleted)) {
                return;
            }
            if (mItemList.stream()
                    .filter(item -> item.getItemName().equals(itemToSetCompleted))
                    .allMatch(item -> item.isBought() == isCompleted)) {
                return;
            }

            DatabaseReference dataReference = getDataReference();
            dataReference.child(mKeyMap.get(itemToSetCompleted))
                    .setValue(new FirebaseItem(itemToSetCompleted, isCompleted));
        }
    }

    @Override
    public void registerStorageEventListener(StorageEvent listener) {
        mEventListeners.add(listener);
    }
}
