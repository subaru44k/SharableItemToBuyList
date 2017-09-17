package com.appsubaruod.sharabletobuylist.util.messages;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class ExpandInputBoxEvent {

    private int mExpansionType;

    public ExpandInputBoxEvent(int expansionType) {
        mExpansionType = expansionType;
    }

    public int getExpansionType() {
        return mExpansionType;
    }

}
