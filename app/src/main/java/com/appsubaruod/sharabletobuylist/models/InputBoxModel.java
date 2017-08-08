package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.util.messages.ExpandInputBoxEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class InputBoxModel {

    private static final String LOG_TAG = InputBoxModel.class.getName();
    private static InputBoxModel mInputBoxModel;

    private Context mContext;
    private String mTextBoxString;
    private static int mExpansionState;

    private InputBoxModel(Context context) {
        mContext = context;
        mTextBoxString = mContext.getResources().getString(R.string.sample_input_text);
        mExpansionState = BottomSheetBehavior.STATE_COLLAPSED;
    }

    public static InputBoxModel getInstance(Context context) {
        if (mInputBoxModel == null) {
            mInputBoxModel = new InputBoxModel(context);
        }
        return mInputBoxModel;
    }

    public static InputBoxModel getInstanceIfCreated() {
        if (mInputBoxModel == null) {
            throw new IllegalStateException();
        }
        return mInputBoxModel;
    }

    public String getTextBoxString() {
        return mTextBoxString;
    }

    /**
     * Controls InputBox and editable state of input box.
     */
    public void changeInputBoxSelectionState() {
        Log.d(LOG_TAG, "changeInputBoxSelectionState : " + mExpansionState);

        switch (mExpansionState) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                mExpansionState = BottomSheetBehavior.STATE_EXPANDED;
                EventBus.getDefault().post(new ExpandInputBoxEvent(mExpansionState));
                break;
            default:
                Log.d(LOG_TAG, "ignore event since input box is not collapsed state");
        }
    }

    public void forceChangeInputBoxSelectionState() {
        Log.d(LOG_TAG, "forceChangeInputBoxSelectionState : " + mExpansionState);

        switch (mExpansionState) {
            case BottomSheetBehavior.STATE_EXPANDED:
                mExpansionState = BottomSheetBehavior.STATE_COLLAPSED;
                EventBus.getDefault().post(new ExpandInputBoxEvent(mExpansionState));
                break;
            default:
                mExpansionState = BottomSheetBehavior.STATE_EXPANDED;
                EventBus.getDefault().post(new ExpandInputBoxEvent(mExpansionState));
                break;
        }

    }
}
