package com.appsubaruod.sharabletobuylist.storage.interpretator;

import android.content.Context;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.models.Item;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.realm.RealmItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by s-yamada on 2017/07/27.
 */
public class RealmInterpretator implements StorageInterpretator {
    private static final String LOG_TAG = RealmInterpretator.class.getName();
    private Set<StorageEvent> mEventListeners = new HashSet<>();

    public RealmInterpretator(Context context) {
        Realm.init(context);
    }

    public void changeRootPath(String rootPath) {
        // TODO implement here if this interpretator should be supported
        throw new IllegalStateException("This method is not supported as of now");
    }

    @Override
    public String createAndGetNewUniqueId() {
        // TODO implement here if this interpretator should be supported
        return null;
    }

    @Override
    public void add(String itemToAdd) {
        Realm realm = Realm.getDefaultInstance();

        // if item already exists in database,
        // check its completed status and if completed then set not completed.
        if (existsItem(realm, itemToAdd)) {
            Log.w(LOG_TAG, "try to add already existing item : " + itemToAdd);
            setCompleted(itemToAdd, false);
            realm.close();
            return;
        }

        realm.beginTransaction();

        RealmItem item = new RealmItem();
        item.setItemName(itemToAdd);
        item.setBought(false);
        realm.insert(item);

        realm.commitTransaction();
        realm.close();

        notifyItemAdded(itemToAdd);
    }

    @Override
    public void removeItem(String itemToDelete) {
        Realm realm = Realm.getDefaultInstance();

        if (!existsItem(realm, itemToDelete)) {
            Log.w(LOG_TAG, "Try to remove not existing item : " + itemToDelete);
            realm.close();
            return;
        }
        realm.beginTransaction();

        RealmResults<RealmItem> results = realm.where(RealmItem.class).equalTo("mItemName", itemToDelete).findAll();
        List<String> deletionList = results.stream().map(result -> result.getItemName()).collect(Collectors.toList());
        results.deleteAllFromRealm();

        realm.commitTransaction();
        realm.close();

        deletionList.forEach(deletedItem -> notifyItemDeleted(deletedItem));
    }

    @Override
    public void removeAllItems() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        List<Item> allItems = getAllItems(realm);
        realm.deleteAll();

        realm.commitTransaction();
        realm.close();

        allItems.forEach(item -> notifyItemDeleted(item.getItemName()));
    }

    private boolean existsItem(Realm realm, String itemName) {
        return (realm.where(RealmItem.class)
                .equalTo("mItemName", itemName).findFirst() != null);
    }

    private void notifyItemDeleted(String itemName) {
        mEventListeners.forEach(listener -> listener.onItemDeleted(itemName));
    }

    private void notifyItemAdded(String itemName) {
        mEventListeners.forEach(listener -> listener.onItemAdded(itemName));
    }

    private void notifyItemCompleted(String itemName, boolean isCompleted) {
        mEventListeners.forEach(listener -> listener.onItemCompleted(itemName, isCompleted));
    }

    private List<Item> getAllItems(Realm realm) {
        RealmResults<RealmItem> results = realm.where(RealmItem.class).findAll();
        return results.stream()
                // for each RealmItem, create Item instance.
                .map(result -> new Item(result.getItemName(), result.isBought()))
                // then collect into List
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        List<Item> items = getAllItems(realm);
        realm.close();
        return items;
    }

    @Override
    public void setCompleted(String itemToSetCompleted, boolean isCompleted) {
        Realm realm = Realm.getDefaultInstance();

        // If item is not exist, nothing to do.
        if (!existsItem(realm, itemToSetCompleted)) {
            Log.w(LOG_TAG,
                    "Try to set completed to not existing item : " + itemToSetCompleted);
            realm.close();
            return;
        }

        // if isCompleted value is already set, ignore.
        RealmItem item = getItem(realm, itemToSetCompleted);
        if (item.isBought() == isCompleted) {
            Log.w(LOG_TAG,
                    "Try to set completed to already set value : " + itemToSetCompleted);
            realm.close();
            return;
        }
        realm.beginTransaction();

        item.setBought(isCompleted);
        realm.insert(item);

        realm.commitTransaction();
        realm.close();

        notifyItemCompleted(itemToSetCompleted, isCompleted);
    }

    private RealmItem getItem(Realm realm, String itemToSetCompleted) {
        return realm.where(RealmItem.class)
                .equalTo("mItemName", itemToSetCompleted).findFirst();
    }


    @Override
    public void registerStorageEventListener(StorageEvent listener) {
        mEventListeners.add(listener);
    }
}
