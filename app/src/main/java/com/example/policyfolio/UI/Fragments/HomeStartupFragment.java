package com.example.policyfolio.UI.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.HomeCallback;
import com.example.policyfolio.ViewModels.HomeViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeStartupFragment extends Fragment {

    private View rootView;
    private HomeViewModel viewModel;

    private LinearLayout addPolicy;

    private HomeCallback callback;

    public HomeStartupFragment() {
    }

    public HomeStartupFragment(HomeCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_startup, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        viewModel.initiateRepo(getContext());

        addPolicy = rootView.findViewById(R.id.add_policy);
        addPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.addPolicy();
            }
        });

        return rootView;
    }

}
