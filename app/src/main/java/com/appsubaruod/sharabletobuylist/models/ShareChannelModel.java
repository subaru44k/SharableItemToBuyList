package com.appsubaruod.sharabletobuylist.models;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

/**
 * Created by s-yamada on 2017/11/24.
 */

public class ShareChannelModel {
    private ChannelModel mChannelModel;
    private static final String LOG_TAG = ShareChannelModel.class.getName();

    public ShareChannelModel() {
        mChannelModel = ChannelModel.getInstanceIfCreated();
    }

    public void doShareChannel(String channelName) {
        String firebaseId = mChannelModel.getFirebaseId(channelName);
        String baseUrl = "https://example.com/";
        Uri linkUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("channelName", channelName)
                .appendQueryParameter("channelId", firebaseId)
                .build();
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(linkUri)
                .setDynamicLinkDomain("d9tb3.app.goo.gl")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Log.d(LOG_TAG, dynamicLink.getUri().toString());
    }
}
