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

    private Context mContext;
    private String mTextBoxString;
    private static int mExpansionState;

    public InputBoxModel() {
    }

    public InputBoxModel(Context context) {
        mContext = context;
        mTextBoxString = mContext.getResources().getString(R.string.sample_input_text);
        mExpansionState = BottomSheetBehavior.STATE_COLLAPSED;
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
