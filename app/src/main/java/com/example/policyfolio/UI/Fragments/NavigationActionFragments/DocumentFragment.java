package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.DocumentCallback;
import com.example.policyfolio.ViewModels.NavigationViewModels.DocumentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentFragment extends Fragment {

    private View rootView;
    private DocumentViewModel viewModel;

    private DocumentCallback callback;

    public DocumentFragment() {
        // Required empty public constructor
    }

    public DocumentFragment(DocumentCallback callback){
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_document, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(DocumentViewModel.class);
        viewModel.initiateRepo(getContext());

        return rootView;
    }

}
