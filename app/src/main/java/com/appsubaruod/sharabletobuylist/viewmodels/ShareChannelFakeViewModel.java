package com.appsubaruod.sharabletobuylist.viewmodels;

import android.app.Activity;

import com.appsubaruod.sharabletobuylist.models.ShareChannelModel;

/**
 * Created by s-yamada on 2017/11/24.
 */

public class ShareChannelFakeViewModel {
    private ShareChannelModel mShareChannelModel;

    public ShareChannelFakeViewModel() {
        mShareChannelModel = new ShareChannelModel();
    }

    public void onClick(Activity activity, String channelName) {
        mShareChannelModel.doShareChannel(activity, channelName);
    }
}
