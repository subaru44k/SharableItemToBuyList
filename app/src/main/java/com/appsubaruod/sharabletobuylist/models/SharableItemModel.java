package com.appsubaruod.sharabletobuylist.models;

import android.graphics.Color;

import com.appsubaruod.sharabletobuylist.state.ActionModeState;
import com.appsubaruod.sharabletobuylist.util.FirebaseEventReporter;
import com.appsubaruod.sharabletobuylist.util.messages.StartActionModeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemModel
        implements SharableItemListModel.BackgroundColorChangedListener, ActionModeState.ActionModeChangedListener {
    private InputBoxModel mInputBoxModel;
    private SharableItemListModel mSharableItemListModel;
    private int mIndex;
    private SharableItemChangedListener mSharableItemChangedListener;
    private boolean mIsItemSelected = false;

    public SharableItemModel(int index) throws IllegalStateException {
        mInputBoxModel = InputBoxModel.getInstanceIfCreated();
        mSharableItemListModel = SharableItemListModel.getInstanceIfCreated();
        mSharableItemListModel.registerBackgroundColorChangedListener(this);
        mSharableItemListModel.registerActionModeChangedListener(this);
        mIndex = index;
    }

    public String getText() {
        return mSharableItemListModel.getText(mIndex);
    }

    /**
     * Called when one of sharable item is clicked.
     */
    public void onItemClicked() {
        if (!mSharableItemListModel.isActionMode()) {
            mInputBoxModel.expandInputBox(true);
            mInputBoxModel.setTextBoxString(mSharableItemListModel.getText(mIndex));
            changeToDefaultColor();
            FirebaseEventReporter.getInstance().sendItemClickedEventLog(getText());
        } else {
            if (isItemSelected()) {
                changeToDefaultColor();
                mSharableItemListModel.unregisterSelectedItem(this);
            } else {
                changeToSelectedColor();
                mSharableItemListModel.registerSelectedItem(this);
            }
            changeSelectedState();
        }
    }

    private boolean isItemSelected() {
        return mIsItemSelected;
    }

    private void changeToSelectedColor() {
        changeBackgroundColor(Color.GRAY);
    }

    private void changeSelectedState() {
        mIsItemSelected = !mIsItemSelected;
    }

    /**
     * Called when one of sharable item is selected.
     */
    public void onItemSelected() {
        if (!mSharableItemListModel.isActionMode()) {
            EventBus.getDefault().post(new StartActionModeEvent());
            changeSelectedState();
            changeToSelectedColor();
            mSharableItemListModel.registerSelectedItem(this);
        }
    }

    private void changeToDefaultColor() {
        changeBackgroundColor(Color.WHITE);
    }

    private void changeBackgroundColor(int color) {
        if (mSharableItemChangedListener != null) {
            mSharableItemChangedListener.onItemColorChanged(color);
        }
    }

    public void setOnSharableItemChangedListener(SharableItemChangedListener listener) {
        mSharableItemChangedListener = listener;
    }

    @Override
    public void onBackgroundColorChanged(int color) {
        changeBackgroundColor(color);
    }

    @Override
    public void onActionModeChanged(boolean isActionMode) {
        if (!isActionMode) {
            mIsItemSelected = false;
        }
    }

    public interface SharableItemChangedListener {
        void onItemColorChanged(int color);
    }
}
