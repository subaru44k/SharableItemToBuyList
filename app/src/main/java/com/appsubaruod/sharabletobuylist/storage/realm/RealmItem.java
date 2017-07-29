package com.appsubaruod.sharabletobuylist.storage.realm;

import io.realm.RealmObject;

/**
 * Created by s-yamada on 2017/07/27.
 */

public class RealmItem extends RealmObject {
    private String mItemName;
    private boolean mIsBought;

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        mItemName = itemName;
    }

    public boolean isBought() {
        return mIsBought;
    }

    public void setBought(boolean bought) {
        mIsBought = bought;
    }
}
