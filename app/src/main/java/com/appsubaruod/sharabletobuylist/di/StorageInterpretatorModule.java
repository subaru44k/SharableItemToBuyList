package com.appsubaruod.sharabletobuylist.di;

import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;
import com.appsubaruod.sharabletobuylist.storage.interpretator.MockInterpretator;

import dagger.Module;
import dagger.Provides;

/**
 * Created by s-yamada on 2017/07/17.
 */
@Module
public class StorageInterpretatorModule {
    @Provides
    public StorageInterpretator provideStorageInterpretator() {
        return new MockInterpretator();
    }
}
