package com.appsubaruod.sharabletobuylist.storage.eventobserver;

import android.util.Log;

import com.appsubaruod.sharabletobuylist.message.ItemAddedEvent;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by s-yamada on 2017/07/17.
 */

public class StorageEventObserver implements StorageInterpretator.StorageEvent {
    private static final String LOG_TAG = StorageEventObserver.class.getName();
    @Override
    public void onItemAdded(String itemAdded) {
        Log.d(LOG_TAG, "Item added : " + itemAdded);
        EventBus.getDefault().postSticky(new ItemAddedEvent(itemAdded));
    }

    @Override
    public void onItemCompleted(String itemCompleted, boolean isCompleted) {

    }

    @Override
    public void onItemDeleted(String itemDeleted) {

    }
}
