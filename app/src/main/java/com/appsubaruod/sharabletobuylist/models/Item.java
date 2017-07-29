package com.appsubaruod.sharabletobuylist.models;

/**
 * Created by s-yamada on 2017/07/21.
 */

public class Item {
    private final String mItemName;
    private boolean mIsBought;

    public Item(String itemName, boolean isBought) {
        mItemName = itemName;
        mIsBought = isBought;
    }

    public String getItemName() {
        return mItemName;
    }

    public boolean isBought() {
        return mIsBought;
    }

    public void setBought(boolean bought) {
        mIsBought = bought;
    }
}
