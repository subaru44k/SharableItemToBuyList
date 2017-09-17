package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.storage.eventoperator.StorageEventOperator;
import com.appsubaruod.sharabletobuylist.util.messages.ItemAddedEvent;
import com.appsubaruod.sharabletobuylist.util.messages.ItemCompletedEvent;
import com.appsubaruod.sharabletobuylist.util.messages.ItemDeletedEvent;
import com.appsubaruod.sharabletobuylist.util.messages.ListItemChangedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by s-yamada on 2017/08/08.
 */
public class SharableItemListModel {
    private static final String LOG_TAG = SharableItemListModel.class.getName();

    private Context mContext;
    private StorageEventOperator mStorageEventOperator;
    private static SharableItemListModel mModel;
    private List<Item> mItemList = new CopyOnWriteArrayList<>();

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

    public void modifyItem(String oldItemName, String newItemName) {
        mStorageEventOperator.getItemsAsync(itemList -> {
            itemList.forEach(item -> Log.d(LOG_TAG, "items : " + item));
        });
        mStorageEventOperator.removeItem(oldItemName);
        mStorageEventOperator.addItem(newItemName);
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
        Log.d(LOG_TAG, "Receive item added : " + event.getItemName());
        mItemList.add(new Item(event.getItemName(), false));
        notifyListItemChanged();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void onItemCompleted(ItemCompletedEvent event) {
        Log.d(LOG_TAG, "Receive item completed : " + event.getItemName() + ", " + event.isCompleted());
        mItemList.removeIf(item -> item.getItemName().equals(event.getItemName()));
        mItemList.add(new Item(event.getItemName(), event.isCompleted()));
        notifyListItemChanged();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void onItemDeleted(ItemDeletedEvent event) {
        Log.d(LOG_TAG, "Receive item deleted : " + event.getItemName());
        mItemList.removeIf(item -> item.getItemName().equals(event.getItemName()));
        notifyListItemChanged();
    }

    private void notifyListItemChanged() {
        EventBus.getDefault().postSticky(new ListItemChangedEvent());
    }

}
