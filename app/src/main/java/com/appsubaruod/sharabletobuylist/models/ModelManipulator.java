package com.appsubaruod.sharabletobuylist.models;

import android.graphics.Color;

/**
 * Created by s-yamada on 2017/10/13.
 */
public class ModelManipulator {
    private SharableItemListModel mSharableItemListModel;
    private InputBoxModel mInputBoxModel;
    private ChannelModel mChannelModel;

    private SharableItemListModel getSharableItemListModel() {
        if (mSharableItemListModel == null) {
            mSharableItemListModel = SharableItemListModel.getInstanceIfCreated();
        }
        return mSharableItemListModel;
    }

    private InputBoxModel getInputBoxModel() {
        if (mInputBoxModel == null) {
            mInputBoxModel = InputBoxModel.getInstanceIfCreated();
        }
        return mInputBoxModel;
    }

    private ChannelModel getChannelModel() {
        if (mChannelModel == null) {
            mChannelModel = ChannelModel.getInstance();
        }
        return mChannelModel;
    }

    public void archiveSelectedItems() {
        mSharableItemListModel.deleteSelectedItemsIfActionMode();
    }

    public void changeToDefaultBackgroundColor() {
        getSharableItemListModel().changeBackgroundColor(Color.WHITE);
    }

    public int getInputBoxExpantionState() {
        return getInputBoxModel().getCurrentExpansionState();
    }

    public void toggleInputBox() {
        getInputBoxModel().toggleInputBox();
    }

    public void setActionMode(boolean isActionMode) {
        getSharableItemListModel().setActionMode(isActionMode);
    }

    public void changeChannel(String channelName) {
        getChannelModel().changeChannel(channelName);
    }

    public void changeToDefaultChannel() {
        getChannelModel().changeToDefaultChannel();
    }
}
