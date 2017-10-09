package com.appsubaruod.sharabletobuylist.util;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by s-yamada on 2017/10/08.
 */

public class FirebasePersistentDatabase {
    private static FirebaseDatabase mInstance;

    private FirebasePersistentDatabase() {
    }

    public static synchronized FirebaseDatabase getInstance() {
        if (mInstance == null) {
            mInstance = FirebaseDatabase.getInstance();
            mInstance.setPersistenceEnabled(true);
        }
        return mInstance;
    }
}
