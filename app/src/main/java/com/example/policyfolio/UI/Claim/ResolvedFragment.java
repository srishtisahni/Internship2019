package com.example.policyfolio.UI.Claim;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.ClaimViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResolvedFragment extends Fragment {

    private View rootView;
    private ClaimViewModel viewModel;

    private RecyclerView claims;

    private TrackClaimCallback callback;

    public ResolvedFragment() {
        // Required empty public constructor
    }

    public ResolvedFragment(ClaimViewModel viewModel, TrackClaimCallback callback){
        this.viewModel = viewModel;
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_resolved, container, false);

        claims = rootView.findViewById(R.id.claims);

        return rootView;
    }

}
