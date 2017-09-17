package com.appsubaruod.sharabletobuylist.storage.eventoperator;

import android.content.Context;

import com.appsubaruod.sharabletobuylist.di.DaggerStorageInterpretatorComponent;
import com.appsubaruod.sharabletobuylist.di.StorageInterpretatorModule;
import com.appsubaruod.sharabletobuylist.models.Item;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.eventobserver.StorageEventObserver;
import com.appsubaruod.sharabletobuylist.util.WorkerThread;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/**
 * Created by s-yamada on 2017/07/17.
 */

public class StorageEventOperator {
    private Context mContext;

    @Inject StorageInterpretator mInterpretator;

    public StorageEventOperator(Context context) {
        mContext = context;
        initialize();
    }

    private ExecutorService getDatabaseExecutor() {
        return WorkerThread.getSingleExecutor();
    }

    private void initialize() {
        mInterpretator = DaggerStorageInterpretatorComponent.builder()
                .storageInterpretatorModule(new StorageInterpretatorModule(mContext)).build().inject();
        mInterpretator.registerStorageEventListener(new StorageEventObserver());
    }

    public void addItem(String itemToAdd) {
        getDatabaseExecutor().submit(() ->
                mInterpretator.add(itemToAdd));
    }

    public void getItemsAsync(ItemsObtainedListener listener) {
        getDatabaseExecutor().submit(() -> {
            List<Item> itemList = mInterpretator.getAllItems();
            listener.onItemsObtained(itemList);
        });
    }

    public void removeItem(String itemToRemove) {
        getDatabaseExecutor().submit(() -> {
           mInterpretator.removeItem(itemToRemove);
        });
    }

    public void removeAllItems() {
        getDatabaseExecutor().submit(() -> {
            mInterpretator.removeAllItems();
        });
    }

    public void setItemCompleted(String itemName, boolean isCompleted) {
        getDatabaseExecutor().submit(() -> {
           mInterpretator.setCompleted(itemName, isCompleted);
        });
    }

    public interface ItemsObtainedListener {
        void onItemsObtained(List<Item> itemList);
    }

}
