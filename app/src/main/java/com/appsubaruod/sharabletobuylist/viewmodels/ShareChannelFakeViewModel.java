package com.appsubaruod.sharabletobuylist.viewmodels;

import com.appsubaruod.sharabletobuylist.models.ShareChannelModel;

/**
 * Created by s-yamada on 2017/11/24.
 */

public class ShareChannelFakeViewModel {
    private ShareChannelModel mShareChannelModel;

    public ShareChannelFakeViewModel() {
        mShareChannelModel = new ShareChannelModel();
    }

    public void onClick(String channelName) {
        mShareChannelModel.doShareChannel(channelName);
    }
}
