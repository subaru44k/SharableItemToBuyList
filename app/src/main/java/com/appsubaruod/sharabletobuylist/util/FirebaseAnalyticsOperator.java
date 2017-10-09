package com.appsubaruod.sharabletobuylist.util;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by s-yamada on 2017/09/17.
 */

public class FirebaseAnalyticsOperator {
    private static FirebaseAnalytics mInstance;

    private FirebaseAnalyticsOperator() {
    }

    public static synchronized FirebaseAnalytics getInstance(Context context) {
        if (mInstance == null) {
            mInstance = FirebaseAnalytics.getInstance(context);
        }
        return mInstance;
    }

    public static FirebaseAnalytics getInstanceIfCreated() {
        if (mInstance == null) {
            throw new IllegalStateException("getInstanceIfCreated() is called without calling getInstance(context).");
        }
        return mInstance;
    }

    public class Param {
        public static final String OLD_ITEM_NAME = "old_item_name";
        public static final String NEW_ITEM_NAME = "new_item_name";
    }

    public class Event {
        public static final String ADD_CONTENT = "add_content";
        public static final String MODIFY_CONTENT = "modify_content";
    }
}
