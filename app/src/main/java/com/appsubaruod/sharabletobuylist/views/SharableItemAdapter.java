package com.appsubaruod.sharabletobuylist.views;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.databinding.SharableItemViewBinding;
import com.appsubaruod.sharabletobuylist.models.SharableItemListModel;
import com.appsubaruod.sharabletobuylist.util.messages.ListItemChangedEvent;
import com.appsubaruod.sharabletobuylist.viewmodels.SharableItemViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by s-yamada on 2017/08/08.
 */

public class SharableItemAdapter extends RecyclerView.Adapter<SharableItemAdapter.ItemViewHolder> {

    private static final String LOG_TAG = SharableItemAdapter.class.getName();
    private final SharableItemListModel mSharableItemListModel;

    public SharableItemAdapter(SharableItemListModel model) {
        mSharableItemListModel = model;
        EventBus.getDefault().register(this);
    }

    @Override
    public SharableItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SharableItemViewBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.sharable_item_view, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SharableItemAdapter.ItemViewHolder holder, int position) {
        SharableItemViewModel model = new SharableItemViewModel(position);
        holder.getSharableItemBinding().setSharableItem(model);
    }

    @Override
    public int getItemCount() {
        return mSharableItemListModel.getItemCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void receiveListItemChanged(ListItemChangedEvent event) {
        notifyDataSetChanged();
        Log.d(LOG_TAG, "notifyDataSetChanged");
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final SharableItemViewBinding mBinding;

        public ItemViewHolder(SharableItemViewBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public SharableItemViewBinding getSharableItemBinding() {
            return mBinding;
        }
    }

}

