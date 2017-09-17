package com.appsubaruod.sharabletobuylist.message;

/**
 * Created by s-yamada on 2017/09/03.
 */

/**
 * Event to notify some items are updated.
 */
public class ItemAddedEvent {
    private String mItemAdded;

    public ItemAddedEvent(String itemAdded) {
        mItemAdded = itemAdded;
    }

    public String getItemAdded() {
        return mItemAdded;
    }
}
