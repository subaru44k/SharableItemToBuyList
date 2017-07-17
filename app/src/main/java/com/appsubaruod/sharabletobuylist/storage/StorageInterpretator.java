package com.appsubaruod.sharabletobuylist.storage;

/**
 * Created by s-yamada on 2017/07/17.
 * Interprets intuitive item operation to the actual storage operations.
 */
public interface StorageInterpretator {
    void add(String itemToAdd);
    void setCompleted(String itemToSetCompleted);
    void registerStorageEventListener(StorageEvent listener);

    interface StorageEvent {
        void onItemAdded(String itemAdded);
        void onItemCompleted(String itemCompleted);
    }
}
