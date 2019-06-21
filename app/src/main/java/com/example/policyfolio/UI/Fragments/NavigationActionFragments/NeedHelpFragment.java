package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.NeedHelpCallback;
import com.example.policyfolio.ViewModels.NavigationViewModels.HelpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NeedHelpFragment extends Fragment {

    private View rootView;
    private HelpViewModel viewModel;

    private NeedHelpCallback callback;

    public NeedHelpFragment() {
        // Required empty public constructor
    }

    public NeedHelpFragment(NeedHelpCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_need_help, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(HelpViewModel.class);
        viewModel.initiateRepo(getContext());

        return rootView;
    }

}
