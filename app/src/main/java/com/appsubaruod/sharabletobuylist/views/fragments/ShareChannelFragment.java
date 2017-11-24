package com.appsubaruod.sharabletobuylist.views.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.appsubaruod.sharabletobuylist.viewmodels.ShareChannelFakeViewModel;

import java.util.ArrayList;

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
                    mShareChannelFakeViewModel.onClick(getActivity(), finalItemArray[i]);
                });
        return builder.create();
    }
}
