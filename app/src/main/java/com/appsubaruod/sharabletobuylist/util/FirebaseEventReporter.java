package com.appsubaruod.sharabletobuylist.util;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by s-yamada on 2017/11/18.
 */

public class FirebaseEventReporter {
    private static FirebaseEventReporter mInstance;

    private FirebaseEventReporter() {}

    public static synchronized FirebaseEventReporter getInstance() {
        if (mInstance == null) {
            mInstance = new FirebaseEventReporter();
        }
        return mInstance;
    }

    public void sendAddItemEventLog(String itemName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        FirebaseAnalyticsOperator.getInstanceIfCreated()
                .logEvent(FirebaseAnalyticsOperator.Event.ADD_CONTENT, bundle);
    }

    public void sendModifyItemEventLog(String oldItemName, String newItemName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsOperator.Param.OLD_ITEM_NAME, oldItemName);
        bundle.putString(FirebaseAnalyticsOperator.Param.NEW_ITEM_NAME, newItemName);
        FirebaseAnalyticsOperator.getInstanceIfCreated()
                .logEvent(FirebaseAnalyticsOperator.Event.MODIFY_CONTENT, bundle);
    }

    public void sendDeleteItemEventLog(String deleteItemName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, deleteItemName);
        FirebaseAnalyticsOperator.getInstanceIfCreated()
                .logEvent(FirebaseAnalyticsOperator.Event.DELETE_CONTENT, bundle);
    }

    public void sendItemClickedEventLog(String clickedItemName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, clickedItemName);
        FirebaseAnalyticsOperator.getInstanceIfCreated()
                .logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
