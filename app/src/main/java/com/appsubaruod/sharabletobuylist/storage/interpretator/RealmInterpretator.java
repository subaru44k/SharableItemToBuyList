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

    @Override
    public void add(String itemToAdd) {
        Realm realm = Realm.getDefaultInstance();

        // if item already exists in database, then do not add and close.
        if (existsItem(realm, itemToAdd)) {
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

    private void notifyItemCompleted(String itemName) {
        mEventListeners.forEach(listener -> listener.onItemCompleted(itemName));
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
    public void setCompleted(String itemToSetCompleted) {
        Realm realm = Realm.getDefaultInstance();

        // If item is not exist, nothing to do.
        if (!existsItem(realm, itemToSetCompleted)) {
            realm.close();
            return;
        }
        realm.beginTransaction();

        RealmItem item = new RealmItem();
        item.setItemName(itemToSetCompleted);
        item.setBought(true);
        realm.insert(item);

        realm.commitTransaction();
        realm.close();

        notifyItemCompleted(itemToSetCompleted);
    }


    @Override
    public void registerStorageEventListener(StorageEvent listener) {
        mEventListeners.add(listener);
    }
}
