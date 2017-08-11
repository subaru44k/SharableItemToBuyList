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
    public void expandInputBox() {
        Log.d(LOG_TAG, "expandInputBox : " + mExpansionState);

        switch (mExpansionState) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                mExpansionState = BottomSheetBehavior.STATE_EXPANDED;
                EventBus.getDefault().post(new ExpandInputBoxEvent(mExpansionState));
                break;
            default:
                Log.d(LOG_TAG, "ignore event since input box is not collapsed state");
        }
    }

    public void toggleInputBox() {
        Log.d(LOG_TAG, "toggleInputBox : " + mExpansionState);

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

    public void forceSetInputBoxExpansionState(int state) {
        switch (state) {
            case BottomSheetBehavior.STATE_DRAGGING:
            case BottomSheetBehavior.STATE_SETTLING:
            case BottomSheetBehavior.STATE_EXPANDED:
            case BottomSheetBehavior.STATE_COLLAPSED:
            case BottomSheetBehavior.STATE_HIDDEN:
                mExpansionState = state;
                EventBus.getDefault().post(new ExpandInputBoxEvent(state));
                break;
            default:
                Log.w(LOG_TAG, "forceSetInputBoxExpansionState(state) is called " +
                        "but state is illegal : " + state);
                break;
        }
    }

    public int getCurrentExpansionState() {
        return mExpansionState;
    }
}
