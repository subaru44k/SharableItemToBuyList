package com.appsubaruod.sharabletobuylist.storage;

import java.util.Map;

/**
 * Created by s-yamada on 2017/11/23.
 */

public interface UserDataStorage {

    /**
     * Writes channel data to storage
     * @param channelDataMap Map of channel name and id in firebase
     */
    void writeChannelData(Map<String, String> channelDataMap);

    /**
     * Reads channel data from storage
     */
    Map<String, String> readChannelData();

}
