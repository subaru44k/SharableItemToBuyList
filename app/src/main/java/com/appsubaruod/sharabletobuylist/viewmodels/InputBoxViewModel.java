package com.appsubaruod.sharabletobuylist.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.appsubaruod.sharabletobuylist.BR;
import com.appsubaruod.sharabletobuylist.models.InputBoxModel;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class InputBoxViewModel extends BaseObservable implements InputBoxModel.OnInputBoxChangedListener {

    private String inputText;
    private boolean mIsOpened;
    private boolean mOpenedFromItem;

    private InputBoxModel mModel;

    public InputBoxViewModel(Context context) {
        mModel = InputBoxModel.getInstance(context);
        setInputText(mModel.getTextBoxString());
        mModel.setOnInputBoxChangedListener(this);
    }

    @Bindable
    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
        notifyPropertyChanged(BR.inputText);
    }

    @Bindable
    public boolean getOpened() {
        return mIsOpened;
    }

    private void setOpened(boolean isOpened) {
        mIsOpened = isOpened;
        notifyPropertyChanged(BR.opened);
    }

    @Bindable
    public boolean getOpenedFromItem() {
        return mOpenedFromItem;
    }

    private void setOpenedFromItem(boolean openedFromItem) {
        mOpenedFromItem = openedFromItem;
        notifyPropertyChanged(BR.openedFromItem);
    }

    /**
     * Called when InputBox is selected.
     *
     * @param view inputbox
     */
    public void onClick(View view) {
        mModel.onClick();
    }

    public void onClickModifyItemButton(View view) {
        mModel.modifyItem(getInputText());
    }

    public void onClickAddItemButton(View view) {
        mModel.addItem(getInputText());
    }

    @Override
    public void onTextChanged(String text) {
        setInputText(text);
    }

    @Override
    public void onInputBoxExpanded(boolean isOpened) {
        setOpened(isOpened);
    }

    @Override
    public void onInputBoxExpanded(boolean isOpened, boolean fromItem) {
        onInputBoxExpanded(isOpened);
        setOpenedFromItem(fromItem);
    }
}
