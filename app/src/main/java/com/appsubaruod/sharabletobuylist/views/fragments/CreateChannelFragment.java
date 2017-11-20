package com.appsubaruod.sharabletobuylist.views.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.databinding.FragmentCreateChannelBinding;
import com.appsubaruod.sharabletobuylist.viewmodels.CreateChannelViewModel;

public class CreateChannelFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentCreateChannelBinding mBinding =
                DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                        R.layout.fragment_create_channel, null, false);
        CreateChannelViewModel createChannelViewModel = new CreateChannelViewModel();
        mBinding.setCreateChannel(createChannelViewModel);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("New channel name")
                .setPositiveButton("OK", (dialog, id) -> createChannelViewModel.create())
                .setNegativeButton("Cancel", (dialog, id) -> createChannelViewModel.cancel());

        return builder.setView(mBinding.getRoot()).create();
    }
}
