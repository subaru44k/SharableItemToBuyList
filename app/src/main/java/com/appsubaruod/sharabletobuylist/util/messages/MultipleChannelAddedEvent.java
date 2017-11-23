package com.appsubaruod.sharabletobuylist.util.messages;

import java.util.Set;

/**
 * Created by s-yamada on 2017/11/23.
 */

public class MultipleChannelAddedEvent {
    private Set<String> mChannelSet;

    public MultipleChannelAddedEvent(Set<String> channelSet) {
        mChannelSet = channelSet;
    }

    public Set<String> getChannelSet() {
        return mChannelSet;
    }
}
