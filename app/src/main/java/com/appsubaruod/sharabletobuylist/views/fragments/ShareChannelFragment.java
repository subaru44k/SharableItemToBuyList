package com.appsubaruod.sharabletobuylist.views.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.models.ShareChannelModel;
import com.appsubaruod.sharabletobuylist.viewmodels.ShareChannelFakeViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShareChannelFragment extends DialogFragment {
    private ShareChannelFakeViewModel mShareChannelFakeViewModel;

    public ShareChannelFragment() {
        if (mShareChannelFakeViewModel == null) {
            mShareChannelFakeViewModel = new ShareChannelFakeViewModel();
        }
    }

    public static ShareChannelFragment newInstance(ArrayList<String> channelList) {
        ShareChannelFragment fragment = new ShareChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("channels", channelList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] itemArray = {};
        itemArray = getArguments().getStringArrayList("channels").toArray(itemArray);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] finalItemArray = itemArray;
        builder.setTitle("Share channel")
                .setItems(itemArray, (dialogInterface, i) -> {
                    mShareChannelFakeViewModel.onClick(finalItemArray[i]);
                });
        return builder.create();
    }
}
