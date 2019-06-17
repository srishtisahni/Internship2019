package com.example.policyfolio.UI.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.NomineeCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class NomineeDashboardFragment extends Fragment {
    private View rootView;

    private RecyclerView nominees;
    private RecyclerView nomineesMe;

    private Button addNominees;

    private NomineeCallback callback;

    public NomineeDashboardFragment() {
        // Required empty public constructor
    }

    public NomineeDashboardFragment(NomineeCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_nominee_dashboard, container, false);

        nominees = rootView.findViewById(R.id.nominees);
        nomineesMe = rootView.findViewById(R.id.me_nominees);

        addNominees = rootView.findViewById(R.id.add_nominee);

        addNominees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.addNominee();
            }
        });

        setUpAdapters();
        
        return rootView;
    }

    private void setUpAdapters() {
    }

}
