package com.appsubaruod.sharabletobuylist.storage.eventobserver;

import android.util.Log;

import com.appsubaruod.sharabletobuylist.util.messages.ItemAddedEvent;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.util.messages.ItemCompletedEvent;
import com.appsubaruod.sharabletobuylist.util.messages.ItemDeletedEvent;

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
        Log.d(LOG_TAG, "Item completed : " + itemCompleted + ", " + isCompleted);
        EventBus.getDefault().postSticky(new ItemCompletedEvent(itemCompleted, isCompleted));
    }

    @Override
    public void onItemDeleted(String itemDeleted) {
        Log.d(LOG_TAG, "Item deleted : " + itemDeleted);
        EventBus.getDefault().postSticky(new ItemDeletedEvent(itemDeleted));
    }
}
