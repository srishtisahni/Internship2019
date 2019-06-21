package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.policyfolio.R;
import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.Util.Adapters.MeNomineeDisplayAdapter;
import com.example.policyfolio.Util.Adapters.NomineeDisplayAdapter;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.NomineeCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.HomeViewModel;
import com.example.policyfolio.ViewModels.NavigationViewModels.NomineeViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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
    private ArrayList<User> meNomineeUsers;
    private HashMap<String, ArrayList<Policy>> meNomineePolicies;
    private MeNomineeDisplayAdapter meNomineeDisplayAdapter;
    private HashMap<Long, InsuranceProvider> providersHashMap;

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
        meNomineeUsers = new ArrayList<>();
        meNomineePolicies = new HashMap<>();
        providersHashMap = new HashMap<>();

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
        viewModel.getProviders().observe(this, new Observer<List<InsuranceProvider>>() {
            @Override
            public void onChanged(List<InsuranceProvider> insuranceProviders) {
                for(int i=0;i<insuranceProviders.size();i++)
                    providersHashMap.put(insuranceProviders.get(i).getId(),insuranceProviders.get(i));
                meNomineeDisplayAdapter.notifyDataSetChanged();
            }
        });
        viewModel.getNominees().observe(this, new Observer<List<Nominee>>() {
            @Override
            public void onChanged(List<Nominee> nominees) {
                nomineesList.clear();
                nomineesList.addAll(nominees);
                nomineeDisplayAdapter.notifyDataSetChanged();
            }
        });
        viewModel.fetchUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user!=null){
                    viewModel.setuEmail(user.getEmail());
                    viewModel.fetchNomineeUsers().observe(NomineeDashboardFragment.this, new Observer<ArrayList<User>>() {
                        @Override
                        public void onChanged(ArrayList<User> users) {
                            meNomineeUsers.clear();
                            meNomineeUsers.addAll(users);
                            meNomineePolicies.clear();
                            for(int i=0;i<meNomineeUsers.size();i++){
                                viewModel.getPoliciesForNominee(users.get(i).getId()).observe(NomineeDashboardFragment.this, new Observer<List<Policy>>() {
                                    @Override
                                    public void onChanged(List<Policy> policies) {
                                        if(policies!=null){
                                            if(policies.size()>0){
                                                String id = policies.get(0).getUserId();
                                                meNomineePolicies.put(id,new ArrayList<Policy>());
                                                meNomineePolicies.get(id).addAll(policies);
                                                meNomineeDisplayAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void setUpAdapters() {
        nomineeDisplayAdapter = new NomineeDisplayAdapter(getContext(), nomineesList);
        nominees.setAdapter(nomineeDisplayAdapter);
        nominees.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

        meNomineeDisplayAdapter = new MeNomineeDisplayAdapter(getContext(),meNomineeUsers,meNomineePolicies,providersHashMap);
        nomineesMe.setAdapter(meNomineeDisplayAdapter);
        nomineesMe.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
    }

}
