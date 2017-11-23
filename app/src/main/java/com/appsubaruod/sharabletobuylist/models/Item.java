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

    public Item(String itemName, String isBought) {
        mItemName = itemName;
        mIsBought = "true".equals(isBought) ? true : false;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        if (this.mItemName.equals(((Item) obj).getItemName())
                && this.mIsBought == ((Item) obj).isBought()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // addition below can overflow, but the result still meets contracts of hashCode().
        // equal object produces same hash code even if it overflows.
        return mItemName.hashCode() + Boolean.valueOf(mIsBought).hashCode();
    }
}
