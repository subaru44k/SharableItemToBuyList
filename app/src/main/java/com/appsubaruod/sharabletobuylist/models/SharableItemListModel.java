package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.message.ItemAddedEvent;
import com.appsubaruod.sharabletobuylist.storage.eventoperator.StorageEventOperator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by s-yamada on 2017/08/08.
 */
public class SharableItemListModel {
    private static final String LOG_TAG = SharableItemListModel.class.getName();

    private Context mContext;
    private StorageEventOperator mStorageEventOperator;
    private static SharableItemListModel mModel;
    private List<Item> mItemList = new ArrayList<>();

    private SharableItemListModel(Context context) {
        mContext = context;
        mStorageEventOperator = new StorageEventOperator(mContext);
        mStorageEventOperator.getItemsAsync(itemList -> {
            mItemList = itemList;
        });

        EventBus.getDefault().register(this);
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

    public void addItem(String itemName) {
        mStorageEventOperator.addItem(itemName);
    }

    public String getText(int index) {
        if (index > mItemList.size() - 1) {
            return "Larger than the list size";
        }
        return mItemList.get(index).getItemName();
    }

    public int getItemCount() {
        return mItemList.size();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void onItemAdded(ItemAddedEvent event) {
        Log.d(LOG_TAG, "Receive item added : " + event.getItemAdded());
        mItemList.add(new Item(event.getItemAdded(), false));
    }

}
