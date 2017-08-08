package com.appsubaruod.sharabletobuylist.util.messages;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class ChangeInputBoxTextEvent {
    private String mText;

    public ChangeInputBoxTextEvent(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }
}
