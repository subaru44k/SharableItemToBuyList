package com.appsubaruod.sharabletobuylist.util.messages;

/**
 * Created by s-yamada on 2017/09/17.
 */

public class ItemDeletedEvent {
    private String mItemName;

    public ItemDeletedEvent(String itemName) {
        mItemName = itemName;
    }

    public String getItemName() {
        return mItemName;
    }
}
