package com.appsubaruod.sharabletobuylist.storage.interpretator;

import android.content.Context;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.models.Item;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by s-yamada on 2017/07/17.
 */

public class MockInterpretator implements StorageInterpretator {
    private static final String LOG_TAG = MockInterpretator.class.getName();
    private ScheduledExecutorService mScheduledService = Executors.newSingleThreadScheduledExecutor();

    private Set<StorageEvent> eventListeners = new HashSet<>();

    public MockInterpretator(Context context) {
        // do not use context
    }

    public void changeRootPath(String rootPath) {

    }

    @Override
    public void add(String itemToAdd) {
        Log.d(LOG_TAG, "Add item : " + itemToAdd);
        mScheduledService.schedule(() -> {
            eventListeners.forEach(item -> item.onItemAdded(itemToAdd));
        }, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void removeItem(String itemToDelete) {

    }

    @Override
    public void removeAllItems() {

    }

    @Override
    public List<Item> getAllItems() {
        return Arrays.asList();
    }

    @Override
    public void setCompleted(String itemToSetCompleted, boolean isCompleted) {
        Log.d(LOG_TAG, "Set completed : " + itemToSetCompleted);
        eventListeners.forEach(item -> item.onItemCompleted(itemToSetCompleted, isCompleted));
    }

    @Override
    public void registerStorageEventListener(StorageEvent listener) {
        eventListeners.add(listener);
    }
}
