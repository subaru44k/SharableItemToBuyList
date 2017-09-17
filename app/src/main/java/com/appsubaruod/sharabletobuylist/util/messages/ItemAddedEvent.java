package com.appsubaruod.sharabletobuylist.util.messages;

/**
 * Created by s-yamada on 2017/09/03.
 */

/**
 * Event to notify some items are updated.
 */
public class ItemAddedEvent {
    private String mItemName;

    public ItemAddedEvent(String itemName) {
        mItemName = itemName;
    }

    public String getItemName() {
        return mItemName;
    }
}
