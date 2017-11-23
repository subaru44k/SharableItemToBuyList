package com.appsubaruod.sharabletobuylist.util.messages;

/**
 * Created by s-yamada on 2017/11/20.
 */

public class ChannelAddedEvent {
    private String mChannelName;

    public ChannelAddedEvent(String channelName) {
        mChannelName = channelName;
    }

    public String getChannelName() {
        return mChannelName;
    }
}
