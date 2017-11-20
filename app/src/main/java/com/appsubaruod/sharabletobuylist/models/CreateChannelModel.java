package com.appsubaruod.sharabletobuylist.models;

/**
 * Created by s-yamada on 2017/11/20.
 */

public class CreateChannelModel {
    private ChannelModel mChannelModel;

    public CreateChannelModel() {
        mChannelModel = new ChannelModel();
        mChannelModel = ChannelModel.getInstance();
    }

    public void createNewChannel(String channelName) {
        mChannelModel.createChannel(channelName);
        mChannelModel.changeChannel(channelName);
    }

    public void cancelCreatingChannel() {
        return;
    }
}
