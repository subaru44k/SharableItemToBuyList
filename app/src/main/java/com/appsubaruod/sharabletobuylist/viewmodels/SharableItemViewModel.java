package com.appsubaruod.sharabletobuylist.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.appsubaruod.sharabletobuylist.BR;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemViewModel extends BaseObservable {

    @Bindable
    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
        notifyPropertyChanged(BR.text);
    }

    private String mText;
}
