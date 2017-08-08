package com.appsubaruod.sharabletobuylist.models;

import com.appsubaruod.sharabletobuylist.util.messages.ChangeInputBoxText;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemModel {
    private InputBoxModel mInputBoxModel;

    public SharableItemModel() {
        mInputBoxModel = new InputBoxModel();
    }

    public void onItemSelected() {
        mInputBoxModel.changeInputBoxSelectionState();
        EventBus.getDefault().post(new ChangeInputBoxText("hoge"));
    }
}
