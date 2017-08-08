package com.appsubaruod.sharabletobuylist.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.appsubaruod.sharabletobuylist.BR;
import com.appsubaruod.sharabletobuylist.models.SharableItemModel;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemViewModel extends BaseObservable {

    private int mIndex;
    private String mText;
    private SharableItemModel mSharableItemModel;

    public SharableItemViewModel(int index) {
        mSharableItemModel = new SharableItemModel(index);
        mIndex = index;
        setText(mSharableItemModel.getText());
    }

    @Bindable
    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
        notifyPropertyChanged(BR.text);
    }

    public void onClick(View view) {
        mSharableItemModel.onItemSelected();
    }

}
