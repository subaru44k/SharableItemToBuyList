package com.appsubaruod.sharabletobuylist.storage.firebase;

/**
 * Created by s-yamada on 2017/09/19.
 */

public class FirebaseItem {
    private String mItemName;
    /**
     * Handle mIsBought with String to write to firebase consistently
     */
    private String mIsBought;

    public FirebaseItem(String itenName, boolean isBought) {
        mItemName = itenName;
        mIsBought = isBought ? "true" : "false";
    }

    public String getItemName() {
        return mItemName;
    }

    public String isBought() {
        return mIsBought;
    }
}
