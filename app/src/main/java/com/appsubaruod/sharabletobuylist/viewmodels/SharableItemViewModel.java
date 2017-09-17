package com.appsubaruod.sharabletobuylist.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;
import android.view.View;

import com.appsubaruod.sharabletobuylist.BR;
import com.appsubaruod.sharabletobuylist.models.SharableItemModel;

/**
 * Created by s-yamada on 2017/08/08.
 */

/**
 * This instance is created from RecyclerAdapter.
 * To avoid memory leakage, this instance should not be referred from any instance except for RecyclerAdapter.
 */
public class SharableItemViewModel extends BaseObservable {

    private static final String LOG_TAG = SharableItemViewModel.class.getName();
    private String mText;
    private SharableItemModel mSharableItemModel;

    public SharableItemViewModel(int index) {
        mSharableItemModel = new SharableItemModel(index);
        Log.d(LOG_TAG, "text : " + mSharableItemModel.getText());
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
