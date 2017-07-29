package com.appsubaruod.sharabletobuylist.storage.interpretator;

import android.content.Context;

import com.appsubaruod.sharabletobuylist.models.Item;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.realm.RealmItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by s-yamada on 2017/07/27.
 */

public class RealmInterpretator implements StorageInterpretator {
    private static final String LOG_TAG = RealmInterpretator.class.getName();
    private Set<StorageEvent> eventListeners = new HashSet<>();

    public RealmInterpretator(Context context) {
        Realm.init(context);
    }

    @Override
    public void add(String itemToAdd) {
        Realm realm = Realm.getDefaultInstance();

        RealmItem item = new RealmItem();
        item.setItemName(itemToAdd);
        item.setBought(false);
        realm.insert(item);

        realm.close();
    }

    @Override
    public List<Item> getAllItems() {
        return null;
    }

    @Override
    public void setCompleted(String itemToSetCompleted) {

    }

    @Override
    public void registerStorageEventListener(StorageEvent listener) {

    }
}
