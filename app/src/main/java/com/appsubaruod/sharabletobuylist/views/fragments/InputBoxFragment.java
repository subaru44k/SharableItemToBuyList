package com.appsubaruod.sharabletobuylist.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.databinding.FragmentInputBoxBinding;
import com.appsubaruod.sharabletobuylist.viewmodels.InputBoxViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InputBoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputBoxFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public InputBoxFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InputBoxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputBoxFragment newInstance() {
        InputBoxFragment fragment = new InputBoxFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentInputBoxBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input_box, container, false);

        InputBoxViewModel model = new InputBoxViewModel(getActivity());
        binding.setInputItem(model);

        return binding.getRoot();
    }

}
