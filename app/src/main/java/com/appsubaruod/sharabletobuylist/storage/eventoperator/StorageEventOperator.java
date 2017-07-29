package com.appsubaruod.sharabletobuylist.storage.eventoperator;

import android.content.Context;

import com.appsubaruod.sharabletobuylist.di.DaggerStorageInterpretatorComponent;
import com.appsubaruod.sharabletobuylist.di.StorageInterpretatorModule;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.eventobserver.StorageEventObserver;
import com.appsubaruod.sharabletobuylist.util.WorkerThread;

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

    private void initialize() {
        mInterpretator = DaggerStorageInterpretatorComponent.builder().
                storageInterpretatorModule(new StorageInterpretatorModule(mContext)).build().inject();
        mInterpretator.registerStorageEventListener(new StorageEventObserver());
    }

    public void add(String itemToAdd) {
        WorkerThread.getSingleExecutor().submit(new Runnable() {
            @Override
            public void run() {
                mInterpretator.add(itemToAdd);
            }
        });
    }

}
