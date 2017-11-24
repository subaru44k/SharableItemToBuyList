package com.appsubaruod.sharabletobuylist.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.appinvite.AppInviteInvitation;
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

    public void doShareChannel(Activity activity, String channelName) {
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
                .buildDynamicLink();

        if (!appInstalled(activity, "jp.naver.line.android")) {
            Intent intent = new AppInviteInvitation.IntentBuilder("Share items with others")
                    .setMessage("Share items with app")
                    .setDeepLink(linkUri)
                    .setCallToActionText("Install app!")
                    .build();
            activity.startActivityForResult(intent, 10000);
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("line://msg/text/" + Uri.encode(dynamicLink.getUri().toString()).toString()));
            activity.startActivity(intent);
        }

        Log.d(LOG_TAG, dynamicLink.getUri().toString());
    }

    public static boolean appInstalled(Context context, String uri)
    {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
