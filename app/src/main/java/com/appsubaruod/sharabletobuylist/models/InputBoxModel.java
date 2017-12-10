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
    private int mExpansionState;

    private SharableItemListModel mSharableItemListModel = SharableItemListModel.getInstanceIfCreated();

    private Set<OnInputBoxChangedListener> mListenerSet = new CopyOnWriteArraySet<>();

    private InputBoxModel(Context context) {
        mContext = context;
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
    void setTextBoxString(String text) {
        mTextBoxString = text;
        mListenerSet.forEach(listener -> listener.onTextChanged(mTextBoxString));
    }

    private void notifyInputBoxExpanded(boolean isExpanded) {
        mListenerSet.forEach(listener -> listener.onInputBoxExpanded(isExpanded));
    }

    private void notifyInputBoxExpanded(boolean isExpanded, boolean fromItem) {
        mListenerSet.forEach(listener -> listener.onInputBoxExpanded(isExpanded, fromItem));
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
        expandInputBox(false);
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
    void expandInputBox(boolean fromItem) {
        Log.d(LOG_TAG, "expandInputBox : " + mExpansionState);

        switch (mExpansionState) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                mExpansionState = BottomSheetBehavior.STATE_EXPANDED;
                EventBus.getDefault().post(new ExpandInputBoxEvent(mExpansionState));
                notifyInputBoxExpanded(true, fromItem);
                break;
            default:
                Log.d(LOG_TAG, "ignore event since input box is not collapsed state");
        }
    }

    void toggleInputBox() {
        Log.d(LOG_TAG, "toggleInputBox : " + mExpansionState);

        switch (mExpansionState) {
            case BottomSheetBehavior.STATE_EXPANDED:
                mExpansionState = BottomSheetBehavior.STATE_COLLAPSED;
                EventBus.getDefault().post(new ExpandInputBoxEvent(mExpansionState));
                EventBus.getDefault().post(new CloseFloatingActionMenuEvent());
                notifyInputBoxExpanded(false);
                break;
            default:
                mExpansionState = BottomSheetBehavior.STATE_EXPANDED;
                EventBus.getDefault().post(new ExpandInputBoxEvent(mExpansionState));
                notifyInputBoxExpanded(true);
                break;
        }
    }

    void forceSetInputBoxExpansionState(int state) {
        switch (state) {
            case BottomSheetBehavior.STATE_EXPANDED:
                mExpansionState = state;
                EventBus.getDefault().post(new ExpandInputBoxEvent(state));
                notifyInputBoxExpanded(true);
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
            case BottomSheetBehavior.STATE_DRAGGING:
            case BottomSheetBehavior.STATE_SETTLING:
            case BottomSheetBehavior.STATE_HIDDEN:
                mExpansionState = state;
                EventBus.getDefault().post(new ExpandInputBoxEvent(state));
                notifyInputBoxExpanded(false);
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
        void onInputBoxExpanded(boolean isOpened);
        void onInputBoxExpanded(boolean isOpened, boolean fromItem);
    }
}
