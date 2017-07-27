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
}
