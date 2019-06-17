package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.R;
import com.example.policyfolio.Util.Adapters.BasicDropdownProviderAdapter;
import com.example.policyfolio.Util.Adapters.BasicDropdownTextAdapter;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.AddPolicyCallback;
import com.example.policyfolio.ViewModels.NavigationViewModels.AddViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicAddPolicyFragment extends Fragment implements BasicDropdownTextAdapter.ParentCallback, BasicDropdownProviderAdapter.ParentCallback {

    private View rootView;
    private AddViewModel viewModel;

    private TextView typeValue;
    private RecyclerView typeChoice;
    private BasicDropdownTextAdapter typeAdapter;
    private String[] insurances;

    private RecyclerView providerChoice;
    private TextView providerText;
    private FrameLayout providerFrame;
    private BasicDropdownProviderAdapter providerAdapter;
    private ArrayList<InsuranceProvider> providers;

    private EditText policyNumber;

    private Button next;

    private LinearLayout divider;
    private ConstraintLayout buy;
    private RecyclerView sellerList;

    private AddPolicyCallback callback;

    public BasicAddPolicyFragment() {
        // Required empty public constructor
    }

    public BasicAddPolicyFragment(AddPolicyCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_basic_add_policy, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(AddViewModel.class);
        viewModel.initiateRepo(getContext());

        typeValue = rootView.findViewById(R.id.policy_type);
        typeChoice = rootView.findViewById(R.id.policy_choice);

        providerFrame = rootView.findViewById(R.id.frame_insurance);
        providerText = rootView.findViewById(R.id.text_insurance);
        providerChoice = rootView.findViewById(R.id.insurance_provider);

        policyNumber = rootView.findViewById(R.id.policy_number);

        next = rootView.findViewById(R.id.next);

        divider = rootView.findViewById(R.id.divider);

        buy = rootView.findViewById(R.id.buy_new);
        sellerList = rootView.findViewById(R.id.policy_sellers);

        setUpViews();
        setDefaultType();

        return rootView;
    }

    private void setDefaultType() {
        int type = getArguments().getInt(Constants.InsuranceProviders.TYPE,-1);
        if(type!=-1)
            setValue(type,Constants.DropDownType.INSURANCE_TYPE);
    }

    private void setUpViews() {
        insurances = getResources().getStringArray(R.array.insurance_type);
        typeAdapter = new BasicDropdownTextAdapter(getContext(),insurances,this,Constants.DropDownType.INSURANCE_TYPE);
        typeChoice.setAdapter(typeAdapter);
        typeChoice.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

        typeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeValue.setVisibility(View.GONE);
                typeChoice.setVisibility(View.VISIBLE);

                providerFrame.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                buy.setVisibility(View.GONE);

                policyNumber.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
            }
        });

        providers = new ArrayList<>();
        providerAdapter = new BasicDropdownProviderAdapter(getContext(),providers,this);
        providerChoice.setAdapter(providerAdapter);
        providerChoice.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

        providerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                providerText.setVisibility(View.GONE);
                providerChoice.setVisibility(View.VISIBLE);
                providerFrame.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                divider.setVisibility(View.GONE);
                buy.setVisibility(View.GONE);

                policyNumber.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
            }
        });

        policyNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() >= 10)
                    next.setVisibility(View.VISIBLE);
                else
                    next.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setPolicyNumber(policyNumber.getText().toString().toUpperCase());
                callback.next();
            }
        });
    }

    @Override
    public void setValue(int position, int type) {
        switch (type){
            case Constants.DropDownType.INSURANCE_TYPE:
                typeValue.setText(insurances[position]);
                typeValue.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                typeValue.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                typeChoice.setVisibility(View.GONE);
                typeValue.setVisibility(View.VISIBLE);

                providerFrame.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                buy.setVisibility(View.VISIBLE);

                providerText.setText("Insurance Provider*");
                providerText.setVisibility(View.VISIBLE);
                providerChoice.setVisibility(View.GONE);
                providerText.setTextColor(getResources().getColor(R.color.Grey));
                providerFrame.setBackground(getResources().getDrawable(R.drawable.dropdown_item_8dp));

                policyNumber.setVisibility(View.GONE);
                policyNumber.setText("");

                viewModel.setType(position).observe(this, new Observer<List<InsuranceProvider>>() {
                    @Override
                    public void onChanged(List<InsuranceProvider> providers) {
                        BasicAddPolicyFragment.this.providers.clear();
                        BasicAddPolicyFragment.this.providers.addAll(providers);
                        providerAdapter.notifyDataSetChanged();
                    }
                });
                break;

            case Constants.DropDownType.INSURANCE_PROVIDER:
                providerText.setText(providers.get(position).getName());
                providerText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                providerChoice.setVisibility(View.GONE);
                providerText.setVisibility(View.VISIBLE);

                policyNumber.setVisibility(View.VISIBLE);
                policyNumber.setText("");

                viewModel.setProvider(providers.get(position));
                break;
        }
    }
}
