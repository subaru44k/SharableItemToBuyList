package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemListModel {
    private Context mContext;

    public SharableItemListModel(Context context) {
        mContext = context;
    }

    public int getItemCount() {
        return 150;
    }
}
