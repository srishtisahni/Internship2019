package com.example.policyfolio.ui.claim;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.policyfolio.R;
import com.example.policyfolio.viewmodels.ClaimViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnGoingFragment extends Fragment {

    private View rootView;
    private ClaimViewModel viewModel;

    private RecyclerView claims;
    private TextView textView;

    private TrackClaimCallback callback;

    public OnGoingFragment() {
        // Required empty public constructor
    }

    public OnGoingFragment(ClaimViewModel viewModel, TrackClaimCallback callback){
        this.viewModel = viewModel;
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_on_going, container, false);

        claims = rootView.findViewById(R.id.claims);
        textView = rootView.findViewById(R.id.text);

        return rootView;
    }

}
