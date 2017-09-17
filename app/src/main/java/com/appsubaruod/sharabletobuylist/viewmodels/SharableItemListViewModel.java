package com.appsubaruod.sharabletobuylist.viewmodels;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.appsubaruod.sharabletobuylist.models.SharableItemListModel;
import com.appsubaruod.sharabletobuylist.views.SharableItemAdapter;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemListViewModel {

    private SharableItemListModel mSharableItemListModel;

    public SharableItemListViewModel(Context context, RecyclerView recyclerView) {
        mSharableItemListModel = SharableItemListModel.getInstance(context);
        recyclerView.setAdapter(new SharableItemAdapter(mSharableItemListModel));
    }
}
