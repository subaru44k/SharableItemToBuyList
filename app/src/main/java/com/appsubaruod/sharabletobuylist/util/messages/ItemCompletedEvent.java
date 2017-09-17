package com.appsubaruod.sharabletobuylist.util.messages;

/**
 * Created by s-yamada on 2017/09/17.
 */

public class ItemCompletedEvent {

    private String mItemName;
    private boolean mIsCompleted;

    public ItemCompletedEvent(String itemName, boolean isCompleted) {
        mItemName = itemName;
        mIsCompleted = isCompleted;
    }

    public String getItemName() {
        return mItemName;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }
}
