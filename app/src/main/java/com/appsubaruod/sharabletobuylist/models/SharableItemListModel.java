package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.state.ActionModeState;
import com.appsubaruod.sharabletobuylist.storage.eventoperator.StorageEventOperator;
import com.appsubaruod.sharabletobuylist.util.Constant;
import com.appsubaruod.sharabletobuylist.util.FirebaseEventReporter;
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
    private Set<SharableItemModel> mSelectedItemModelSet = new HashSet<>();
    private Set<BackgroundColorChangedListener> mBackgroundColorChangedListeners = new HashSet<>();
    private ActionModeState mActionModeState;

    private SharableItemListModel(Context context) {
        mContext = context;
        mStorageEventOperator = new StorageEventOperator(mContext);
        obtainItemsAsync();
        mActionModeState = new ActionModeState();

        EventBus.getDefault().register(this);
    }

    private void obtainItemsAsync() {
        mStorageEventOperator.getItemsAsync(itemList -> {
            // Reverse list to show newer items to the top
            Collections.reverse(itemList);
            mItemList = itemList;
            clearBackgroundColorChangeListenerSet();
            clearActionModeStateListener();
            notifyListItemChanged();
        });
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

    void addItem(String itemName) {
        mStorageEventOperator.addItem(itemName);
        FirebaseEventReporter.getInstance().sendAddItemEventLog(itemName);
    }

    void modifyItem(String oldItemName, String newItemName) {
        mStorageEventOperator.removeItem(oldItemName);
        mStorageEventOperator.addItem(newItemName);
        FirebaseEventReporter.getInstance().sendModifyItemEventLog(oldItemName, newItemName);
    }

    void deleteItem(String itemName) {
        mStorageEventOperator.removeItem(itemName);
        FirebaseEventReporter.getInstance().sendDeleteItemEventLog(itemName);
    }

    void deleteSelectedItemsIfActionMode() {
        if (mActionModeState.isActionMode()) {
            collectSelectedItems().forEach(item -> deleteItem(item.getText()));
        } else {
            Log.w(LOG_TAG, "ignore items deletion, since ActionMode is not started.");
        }
    }

    private Set<SharableItemModel> collectSelectedItems() {
        return mSelectedItemModelSet;
    }

    String getText(int index) {
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
        clearBackgroundColorChangeListenerSet();
        clearActionModeStateListener();
        notifyListItemChanged();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void onItemCompleted(ItemCompletedEvent event) {
        Log.d(LOG_TAG, "Receive item completed : " + event.getItemName() + ", " + event.isCompleted());
        mItemList.remove(getIndexOfTheItem(event.getItemName()));
        mItemList.add(0, new Item(event.getItemName(), event.isCompleted()));
        clearBackgroundColorChangeListenerSet();
        clearActionModeStateListener();
        notifyListItemChanged();
    }

    void registerSelectedItem(SharableItemModel itemModel) {
        mSelectedItemModelSet.add(itemModel);
    }

    void unregisterSelectedItem(SharableItemModel itemModel) {
        mSelectedItemModelSet.remove(itemModel);
    }

    private void clearSelectedItemSet() {
        mSelectedItemModelSet.clear();
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
        clearBackgroundColorChangeListenerSet();
        clearActionModeStateListener();
        notifyListItemChanged();
    }

    private void notifyListItemChanged() {
        EventBus.getDefault().postSticky(new ListItemChangedEvent());
    }

    void registerBackgroundColorChangedListener(BackgroundColorChangedListener listener) {
        mBackgroundColorChangedListeners.add(listener);
    }

    void registerActionModeChangedListener(ActionModeState.ActionModeChangedListener listener) {
        mActionModeState.registerActionModeChangedListener(listener);
    }

    private void clearBackgroundColorChangeListenerSet() {
        mBackgroundColorChangedListeners.clear();
    }

    private void clearActionModeStateListener() {
        if (mActionModeState != null) {
            mActionModeState.clearListener();
        }
    }

    void changeBackgroundColor(int color) {
        mBackgroundColorChangedListeners.forEach(item -> item.onBackgroundColorChanged(color));
    }

    boolean isActionMode() {
        return mActionModeState.isActionMode();
    }

    void setActionMode(boolean isActionMode) {
        mActionModeState.setActionMode(isActionMode);
        // clear selected item set when ActionMode is finished
        if (!isActionMode) {
            clearSelectedItemSet();
        }
    }

    String createAndGetUniqueChannel() {
        return mStorageEventOperator.createAndGetNewUniqueChannel();
    }

    void changeRootPath(String rootPath) {
        mStorageEventOperator.changeRootPath(Constant.SHARABLE_CHANNEL_ROOT_PATH + "/"  + rootPath);
        obtainItemsAsync();
    }

    interface BackgroundColorChangedListener {
        void onBackgroundColorChanged(int color);
    }
}
