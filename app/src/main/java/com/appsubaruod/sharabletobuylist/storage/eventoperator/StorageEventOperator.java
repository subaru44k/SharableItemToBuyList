package com.appsubaruod.sharabletobuylist.storage.eventoperator;

import com.appsubaruod.sharabletobuylist.di.DaggerStorageInterpretatorComponent;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.eventobserver.StorageEventObserver;
import com.appsubaruod.sharabletobuylist.storage.interpretator.MockInterpretator;

import javax.inject.Inject;

/**
 * Created by s-yamada on 2017/07/17.
 */

public class StorageEventOperator {
    private static StorageEventOperator mInstance;

    @Inject StorageInterpretator mInterpretator;

    private StorageEventOperator() {
        initialize();
    }

    private void initialize() {

        mInterpretator = DaggerStorageInterpretatorComponent.create().inject();
        mInterpretator.registerStorageEventListener(new StorageEventObserver());
    }

    public static synchronized StorageEventOperator getInstance() {
        if (mInstance == null) {
            mInstance = new StorageEventOperator();
        }
        return mInstance;
    }

}
