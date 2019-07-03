package com.example.policyfolio.UI.Claim;


import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.WithUser.ClaimViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClaimDashboardFragment extends Fragment {

    private View rootView;
    private ClaimViewModel viewModel;

    private CardView claimAssistance;
    private CardView legalSupport;
    private CardView trackClaim;
    private CardView callAssistance;

    private ClaimCallback callback;

    public ClaimDashboardFragment() {
        // Required empty public constructor
    }

    public ClaimDashboardFragment(ClaimCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_claim_dashboard, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(ClaimViewModel.class);
        viewModel.initiateRepo(getContext());

        claimAssistance = rootView.findViewById(R.id.claim_assitance);
        legalSupport = rootView.findViewById(R.id.legal_support);
        trackClaim = rootView.findViewById(R.id.track_claim);
        callAssistance = rootView.findViewById(R.id.call_assistance);

        setUpListeners();

        return rootView;
    }

    private void setUpListeners() {
        claimAssistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.claimAssistance();
            }
        });

        legalSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.legalSupport();
            }
        });

        trackClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.trackClaim();
            }
        });

        callAssistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callAssistance();
            }
        });
    }

}
