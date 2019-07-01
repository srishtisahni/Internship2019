package com.example.policyfolio.UI.Home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.policyfolio.R;
import com.example.policyfolio.Data.Local.Classes.InsuranceProvider;
import com.example.policyfolio.Data.Local.Classes.Policy;
import com.example.policyfolio.UI.Adapters.ListAdapters.PolicyDisplayAdapter;
import com.example.policyfolio.UI.Adapters.ListAdapters.YellowTextAdapter;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.WithUser.HomeViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.policyfolio.Util.Constants.Policy.Type.APPLIANCE_INSURANCE;
import static com.example.policyfolio.Util.Constants.Policy.Type.LIFE_INSURANCE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePoliciesFragment extends Fragment implements PolicyDisplayAdapter.ParentCallback {

    private View rootView;

    private HomeViewModel viewModel;

    private TextView number;
    private TextView cover;
    private TextView coverDecimal;

    private RecyclerView dues;
    private YellowTextAdapter duesAdapter;
    private RecyclerView returns;
    private YellowTextAdapter returnsAdapter;

    private RecyclerView policies;
    private PolicyDisplayAdapter policyDisplayAdapter;
    private ArrayList<ArrayList<Policy>> typeWisePolicyList;

    private HomeCallback callback;
    private HashMap<Long, InsuranceProvider> providersHashMap;

    private ArrayList<Policy> result;


    public HomePoliciesFragment() {
        // Required empty public constructor
    }

    public HomePoliciesFragment(HomeCallback callback) {
        this.callback = callback;
        typeWisePolicyList = new ArrayList<>();
        providersHashMap = new HashMap<Long, InsuranceProvider>();
        result = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_policies, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        viewModel.initiateRepo(getContext());

        number = rootView.findViewById(R.id.number);
        cover = rootView.findViewById(R.id.amount);
        coverDecimal = rootView.findViewById(R.id.amount_decimal);

        dues = rootView.findViewById(R.id.dues);
        returns = rootView.findViewById(R.id.returns);
        policies = rootView.findViewById(R.id.policies);

        setAdapters();
        addObservers();
        updateChanges();

        return rootView;
    }

    private void setAdapters() {
        policyDisplayAdapter = new PolicyDisplayAdapter(getContext(), typeWisePolicyList, providersHashMap, this);
        duesAdapter = new YellowTextAdapter(getContext(),result,providersHashMap, Constants.Policy.DISPLAY_PREMIUM);
        returnsAdapter = new YellowTextAdapter(getContext(),result,providersHashMap,Constants.Policy.DISPLAY_SUM);

        dues.setAdapter(duesAdapter);
        returns.setAdapter(returnsAdapter);
        policies.setAdapter(policyDisplayAdapter);

        dues.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        returns.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        policies.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }

    public void addObservers() {
        viewModel.getProviders().observe(this, new Observer<List<InsuranceProvider>>() {
            @Override
            public void onChanged(List<InsuranceProvider> insuranceProviders) {
                for(int i=0;i<insuranceProviders.size();i++)
                    providersHashMap.put(insuranceProviders.get(i).getId(),insuranceProviders.get(i));
                policyDisplayAdapter.notifyDataSetChanged();
                duesAdapter.notifyDataSetChanged();
                returnsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void updateChanges() {
        duesAdapter.updatePolicies(result);
        returnsAdapter.updatePolicies(result);

        Double totalCover = Policy.totalCover(result);
        int cover = (int) Math.floor(totalCover);
        int coverDecimal = (int) Math.floor((totalCover - cover)*100);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(cover);
        this.cover.setText(moneyString.substring(1,moneyString.lastIndexOf(".00")));
        if(coverDecimal/10==0){
            this.coverDecimal.setText(".0"+coverDecimal);
        }
        else {
            this.coverDecimal.setText("."+coverDecimal);
        }

        this.number.setText(result.size()+"");

        typeWisePolicyList.clear();
        for (int i = LIFE_INSURANCE; i <= APPLIANCE_INSURANCE; i++) {
            typeWisePolicyList.add(new ArrayList<Policy>());
        }
        for (int i = 0; i < result.size(); i++) {
            Policy policy = result.get(i);
            typeWisePolicyList.get(policy.getType()).add(policy);
        }

        policyDisplayAdapter.notifyDataSetChanged();
        duesAdapter.notifyDataSetChanged();
        returnsAdapter.notifyDataSetChanged();
    }


    @Override
    public void add(int type) {
        callback.addPolicy(type);
    }

    public void setPolicies(List<Policy> policies) {
        if(result == null)
            result = new ArrayList<>();
        result.clear();
        result.addAll(policies);

        if(duesAdapter!=null && returnsAdapter!=null && policyDisplayAdapter!=null)
            updateChanges();
    }
}
