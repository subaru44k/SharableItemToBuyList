package com.appsubaruod.sharabletobuylist.models;

import android.graphics.Color;
import android.os.Bundle;

import com.appsubaruod.sharabletobuylist.util.FirebaseAnalyticsOperator;
import com.appsubaruod.sharabletobuylist.util.messages.StartActionModeEvent;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemModel implements SharableItemListModel.BackgroundColorChangeListener {
    private InputBoxModel mInputBoxModel;
    private SharableItemListModel mSharableItemListModel;
    private int mIndex;
    private SharableItemChangedListener mSharableItemChangedListener;

    public SharableItemModel(int index) throws IllegalStateException {
        mInputBoxModel = InputBoxModel.getInstanceIfCreated();
        mSharableItemListModel = SharableItemListModel.getInstanceIfCreated();
        mSharableItemListModel.registerSharableItemModel(this);
        mIndex = index;
    }

    public String getText() {
        return mSharableItemListModel.getText(mIndex);
    }

    /**
     * Called when one of sharable item is clicked.
     */
    public void onItemClicked() {
        mInputBoxModel.expandInputBox();
        mInputBoxModel.setTextBoxString(mSharableItemListModel.getText(mIndex));
        changeToDefaultColor();

        sendItemSelectedEventLog();
    }

    /**
     * Called when one of sharable item is selected.
     */
    public void onItemSelected() {
        EventBus.getDefault().post(new StartActionModeEvent());
        changeBackgroundColor(Color.GRAY);
    }

    private void changeToDefaultColor() {
        changeBackgroundColor(Color.WHITE);
    }

    private void changeBackgroundColor(int color) {
        if (mSharableItemChangedListener != null) {
            mSharableItemChangedListener.onItemColorChanged(color);
        }
    }

    private void sendItemSelectedEventLog() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getText());
        FirebaseAnalyticsOperator.getInstanceIfCreated()
                .logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void setOnSharableItemChangedListener(SharableItemChangedListener listener) {
        mSharableItemChangedListener = listener;
    }

    @Override
    public void onBackgroundColorChanged(int color) {
        changeBackgroundColor(color);
    }

    public interface SharableItemChangedListener {
        void onItemColorChanged(int color);
    }
}
