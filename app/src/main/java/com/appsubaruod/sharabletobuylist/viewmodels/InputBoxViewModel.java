package com.appsubaruod.sharabletobuylist.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.appsubaruod.sharabletobuylist.BR;
import com.appsubaruod.sharabletobuylist.models.InputBoxModel;
import com.appsubaruod.sharabletobuylist.util.messages.ChangeInputBoxTextEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class InputBoxViewModel extends BaseObservable implements InputBoxModel.OnInputBoxChangedListener {

    private String inputText;

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

    /**
     * Called when InputBox is selected.
     * @param view inputbox
     */
    public void onClick(View view) {
        mModel.expandInputBox();
    }

    public void onClickAddItemButton(View view) {
        mModel.addItem(inputText);
    }

    @Override
    public void onTextChanged(String text) {
        setInputText(text);
    }
}
