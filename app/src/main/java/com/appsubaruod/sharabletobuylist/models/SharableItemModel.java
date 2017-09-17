package com.appsubaruod.sharabletobuylist.models;

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
    }
}
