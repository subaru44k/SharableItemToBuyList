package com.appsubaruod.sharabletobuylist.di;

import android.content.Context;

import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.interpretator.MockInterpretator;
import com.appsubaruod.sharabletobuylist.storage.interpretator.RealmInterpretator;

import dagger.Module;
import dagger.Provides;

/**
 * Created by s-yamada on 2017/07/17.
 */
@Module
public class StorageInterpretatorModule {
    private Context mContext;

    public StorageInterpretatorModule(Context context) {
        mContext = context;
    }

    @Provides
    public StorageInterpretator provideStorageInterpretator() {
        return new MockInterpretator(mContext);
    }
}
