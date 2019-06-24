package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.NavigationViewModels.PromotionViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromotionsFragment extends Fragment {

    private View rootView;
    private PromotionViewModel viewModel;

    public PromotionsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_promotions, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(PromotionViewModel.class);
        viewModel.initiateRepo(getContext());

        return rootView;
    }

}
