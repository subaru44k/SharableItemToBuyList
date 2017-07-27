package com.appsubaruod.sharabletobuylist.di;

import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;

import dagger.Component;

/**
 * Created by s-yamada on 2017/07/17.
 */
@Component(modules = {StorageInterpretatorModule.class})
public interface StorageInterpretatorComponent {
    StorageInterpretator inject();
}
