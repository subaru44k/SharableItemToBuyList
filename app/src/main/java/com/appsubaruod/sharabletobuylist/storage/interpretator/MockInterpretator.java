package com.appsubaruod.sharabletobuylist.storage.interpretator;

import android.util.Log;

import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by s-yamada on 2017/07/17.
 */

public class MockInterpretator implements StorageInterpretator {
    private static final String LOG_TAG = MockInterpretator.class.getName();
    private static StorageInterpretator mInstance;

    private Set<StorageEvent> eventListeners = new HashSet<>();

    private MockInterpretator() {
    }

    /**
     * Obtains the instance of MockInterpretator.
     * @return instance of MockInterpretator.
     */
    public static synchronized StorageInterpretator getInstance() {
        if (mInstance == null) {
            mInstance = new MockInterpretator();
        }
        return mInstance;
    }

    @Override
    public void add(String itemToAdd) {
        Log.d(LOG_TAG, "Add item : " + itemToAdd);
        eventListeners.forEach(item -> item.onItemAdded(itemToAdd));
    }

    @Override
    public void setCompleted(String itemToSetCompleted) {
        Log.d(LOG_TAG, "Set completed : " + itemToSetCompleted);
        eventListeners.forEach(item -> item.onItemCompleted(itemToSetCompleted));
    }

    @Override
    public void registerStorageEventListener(StorageEvent listener) {
        eventListeners.add(listener);
    }
}
