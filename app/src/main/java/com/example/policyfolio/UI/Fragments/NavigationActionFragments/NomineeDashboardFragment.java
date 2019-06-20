package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.policyfolio.R;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Util.Adapters.NomineeDisplayAdapter;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.NomineeCallback;
import com.example.policyfolio.ViewModels.HomeViewModel;
import com.example.policyfolio.ViewModels.NavigationViewModels.NomineeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NomineeDashboardFragment extends Fragment {
    private View rootView;
    private NomineeViewModel viewModel;

    private RecyclerView nominees;
    private NomineeDisplayAdapter nomineeDisplayAdapter;
    private ArrayList<Nominee> nomineesList;

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
        viewModel = ViewModelProviders.of(getActivity()).get(NomineeViewModel.class);
        viewModel.initiateRepo(getContext());

        nominees = rootView.findViewById(R.id.nominees);
        nomineesList = new ArrayList<>();

        nomineesMe = rootView.findViewById(R.id.me_nominees);
        addNominees = rootView.findViewById(R.id.add_nominee);

        addNominees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.addNominee();
            }
        });

        setUpAdapters();
        setObservers();
        
        return rootView;
    }

    private void setObservers() {
        viewModel.getNominees().observe(this, new Observer<List<Nominee>>() {
            @Override
            public void onChanged(List<Nominee> nominees) {
                nomineesList.clear();
                nomineesList.addAll(nominees);
                nomineeDisplayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setUpAdapters() {
        nomineeDisplayAdapter = new NomineeDisplayAdapter(getContext(), nomineesList);
        nominees.setAdapter(nomineeDisplayAdapter);
        nominees.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
    }

}
