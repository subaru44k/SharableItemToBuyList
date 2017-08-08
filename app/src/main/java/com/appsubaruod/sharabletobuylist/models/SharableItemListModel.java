package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemListModel {
    private Context mContext;
    private static SharableItemListModel mModel;

    private SharableItemListModel(Context context) {
        mContext = context;
    }

    public static SharableItemListModel getInstance(Context context) {
        if (mModel == null) {
            mModel = new SharableItemListModel(context);
        }
        return mModel;
    }

    public static SharableItemListModel getInstanceIfCreated() {
        if (mModel == null) {
            throw new IllegalStateException();
        }
        return mModel;
    }

    public String getText(int index) {
        return "Item " + index;
    }

    public int getItemCount() {
        return 150;
    }
}
