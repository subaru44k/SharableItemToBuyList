package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;
import android.graphics.Color;

import java.util.List;

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
            mChannelModel = ChannelModel.getInstanceIfCreated();
        }
        return mChannelModel;
    }

    public void cancelNotification() {
        getSharableItemListModel().cancelNotification();
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

    public void initializeChannelModel(Context applicationContext) {
        mChannelModel = ChannelModel.getInstance(applicationContext);
    }

    public void addChannel(String channelName, String channelId) {
        mChannelModel.addChannelIfNotExist(channelName, channelId);
    }

    public List<String> getChannelList() {
        return mChannelModel.getChannelList();
    }
}
