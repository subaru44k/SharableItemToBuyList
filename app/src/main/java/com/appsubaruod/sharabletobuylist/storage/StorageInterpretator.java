package com.appsubaruod.sharabletobuylist.storage;

import com.appsubaruod.sharabletobuylist.models.Item;

import java.util.List;

/**
 * Created by s-yamada on 2017/07/17.
 * Interprets intuitive item operation to the actual storage operations.
 */
public interface StorageInterpretator {
    void changeRootPath(String rootPath);

    void add(String itemToAdd);

    void removeItem(String itemToDelete);

    void removeAllItems();

    List<Item> getAllItems();

    void setCompleted(String itemToSetCompleted, boolean isCompleted);

    void registerStorageEventListener(StorageEvent listener);

    interface StorageEvent {
        void onItemAdded(String itemAdded);

        void onItemCompleted(String itemCompleted, boolean isCompleted);

        void onItemDeleted(String itemDeleted);
    }
}
