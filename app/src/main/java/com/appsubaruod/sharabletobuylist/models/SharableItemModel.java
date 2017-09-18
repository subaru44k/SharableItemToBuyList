package com.appsubaruod.sharabletobuylist.models;

import android.os.Bundle;

import com.appsubaruod.sharabletobuylist.util.FirebaseAnalyticsOperator;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemModel {
    private InputBoxModel mInputBoxModel;
    private SharableItemListModel mSharableItemListModel;
    private int mIndex;

    public SharableItemModel(int index) throws IllegalStateException {
        mInputBoxModel = InputBoxModel.getInstanceIfCreated();
        mSharableItemListModel = SharableItemListModel.getInstanceIfCreated();
        mIndex = index;
    }

    public String getText() {
        return mSharableItemListModel.getText(mIndex);
    }

    /**
     * Called when one of sharable item is selected.
     */
    public void onItemSelected() {
        mInputBoxModel.expandInputBox();
        mInputBoxModel.setTextBoxString(mSharableItemListModel.getText(mIndex));

        sendItemSelectedEventLog();
    }

    private void sendItemSelectedEventLog() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getText());
        FirebaseAnalyticsOperator.getInstanceIfCreated()
                .logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
