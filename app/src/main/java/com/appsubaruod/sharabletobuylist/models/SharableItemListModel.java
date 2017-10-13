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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private Set<BackgroundColorChangeListener> mBackgroundColorChangeListenerSet = new HashSet<>();

    private SharableItemListModel(Context context) {
        mContext = context;
        mStorageEventOperator = new StorageEventOperator(mContext);
        mStorageEventOperator.getItemsAsync(itemList -> {
            // Reverse list to show newer items to the top
            Collections.reverse(itemList);
            mItemList = itemList;
            clarBackgroundColorChangeListenerSet();
            notifyListItemChanged();
        });

        EventBus.getDefault().register(this);
    }

    public static synchronized SharableItemListModel getInstance(Context context) {
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
        mItemList.add(0, new Item(event.getItemName(), false));
        clarBackgroundColorChangeListenerSet();
        notifyListItemChanged();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void onItemCompleted(ItemCompletedEvent event) {
        Log.d(LOG_TAG, "Receive item completed : " + event.getItemName() + ", " + event.isCompleted());
        mItemList.remove(getIndexOfTheItem(event.getItemName()));
        mItemList.add(0, new Item(event.getItemName(), event.isCompleted()));
        clarBackgroundColorChangeListenerSet();
        notifyListItemChanged();
    }

    private int getIndexOfTheItem(String itemName) {
        final int[] index = new int[1];
        mItemList.forEach(item -> {
            if (item.getItemName().equals(itemName)) {
                index[0] = mItemList.indexOf(item);
            }
        });
        return index[0];
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void onItemDeleted(ItemDeletedEvent event) {
        Log.d(LOG_TAG, "Receive item deleted : " + event.getItemName());
        mItemList.remove(getIndexOfTheItem(event.getItemName()));
        clarBackgroundColorChangeListenerSet();
        notifyListItemChanged();
    }

    private void notifyListItemChanged() {
        EventBus.getDefault().postSticky(new ListItemChangedEvent());
    }

    public void registerSharableItemModel(BackgroundColorChangeListener listener) {
        mBackgroundColorChangeListenerSet.add(listener);
    }

    private void clarBackgroundColorChangeListenerSet() {
        mBackgroundColorChangeListenerSet.clear();
    }

    public void changeBackgroundColor(int color) {
        mBackgroundColorChangeListenerSet.forEach(item -> item.onBackgroundColorChanged(color));
    }

    interface BackgroundColorChangeListener {
        void onBackgroundColorChanged(int color);
    }

}
