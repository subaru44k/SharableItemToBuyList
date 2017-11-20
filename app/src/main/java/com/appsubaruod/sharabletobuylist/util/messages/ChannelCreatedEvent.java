package com.appsubaruod.sharabletobuylist.util.messages;

/**
 * Created by s-yamada on 2017/11/20.
 */

public class ChannelCreatedEvent {
    private String mChannelName;

    public ChannelCreatedEvent(String channelName) {
        mChannelName = channelName;
    }

    public String getChannelName() {
        return mChannelName;
    }
}
