package com.example.policyfolio.ui.empty;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.R;
import com.example.policyfolio.viewmodels.EmptyViewModel;
import com.example.policyfolio.viewmodels.PromotionViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyFragment extends Fragment {

    private View rootView;
    private EmptyViewModel viewModel;

    public EmptyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_empty, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(EmptyViewModel.class);
        viewModel.initiateRepo(getContext());

        return rootView;
    }

}
