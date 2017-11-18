package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.util.messages.CloseFloatingActionMenuEvent;
import com.appsubaruod.sharabletobuylist.util.messages.ExpandInputBoxEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class InputBoxModel {

    private static final String LOG_TAG = InputBoxModel.class.getName();
    private static InputBoxModel mInputBoxModel;

    private Context mContext;
    private String mTextBoxString;
    private static int mExpansionState;

    private SharableItemListModel mSharableItemListModel = SharableItemListModel.getInstanceIfCreated();

    private Set<OnInputBoxChangedListener> mListenerSet = new CopyOnWriteArraySet<>();

    private InputBoxModel(Context context) {
        mContext = context;
        mTextBoxString = mContext.getResources().getString(R.string.sample_input_text);
        mExpansionState = BottomSheetBehavior.STATE_COLLAPSED;
    }

    public static synchronized InputBoxModel getInstance(Context context) {
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

    public void setOnInputBoxChangedListener(OnInputBoxChangedListener listener) {
        mListenerSet.add(listener);
    }

    public String getTextBoxString() {
        return mTextBoxString;
    }

    /**
     * Sets text in the text box.
     *
     * @param text new text set to the box
     */
    public void setTextBoxString(String text) {
        mTextBoxString = text;
        mListenerSet.stream().forEach(listener -> listener.onTextChanged(mTextBoxString));
    }

    /**
     * Add item and reflect to db.
     *
     * @param itemName name of the new item
     */
    public void addItem(String itemName) {
        if (mTextBoxString.equals(itemName)) {
            toggleInputBox();
            return;
        }
        Log.d(LOG_TAG, "Add text : " + mTextBoxString + " -> " + itemName);
        mSharableItemListModel.addItem(itemName);
        toggleInputBox();
    }

    /**
     * Modifies item and reflect to db.
     *
     * @param itemName name of the new item
     */
    public void modifyItem(String itemName) {
        if (mTextBoxString.equals(itemName)) {
            toggleInputBox();
            return;
        }
        Log.d(LOG_TAG, "Modify text : " + mTextBoxString + " -> " + itemName);
        mSharableItemListModel.modifyItem(mTextBoxString, itemName);
        toggleInputBox();
    }

    /**
     * Called when InputBox is clicked.
     */
    public void onClick() {
        if (isInputBoxCollapsed()) {
            setTextBoxString("");
        }
        expandInputBox();
    }

    private boolean isInputBoxExpanded() {
        return mExpansionState == BottomSheetBehavior.STATE_EXPANDED;
    }

    private boolean isInputBoxCollapsed() {
        return mExpansionState == BottomSheetBehavior.STATE_COLLAPSED;
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
                EventBus.getDefault().post(new CloseFloatingActionMenuEvent());
                setTextBoxString(mContext.getResources().getString(R.string.sample_input_text));
                break;
            default:
                mExpansionState = BottomSheetBehavior.STATE_EXPANDED;
                EventBus.getDefault().post(new ExpandInputBoxEvent(mExpansionState));
                break;
        }
    }

    public void forceSetInputBoxExpansionState(int state) {
        switch (state) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                setTextBoxString(mContext.getResources().getString(R.string.sample_input_text));
                // fall through
            case BottomSheetBehavior.STATE_EXPANDED:
            case BottomSheetBehavior.STATE_DRAGGING:
            case BottomSheetBehavior.STATE_SETTLING:
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

    public interface OnInputBoxChangedListener {
        void onTextChanged(String text);
    }
}
