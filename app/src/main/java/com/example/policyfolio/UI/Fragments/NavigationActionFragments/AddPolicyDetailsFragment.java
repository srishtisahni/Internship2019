package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.Nominee;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.Adapters.BasicDropdownNomineeAdapter;
import com.example.policyfolio.UI.Adapters.BasicDropdownTextAdapter;
import com.example.policyfolio.UI.CallBackListeners.AddPolicyCallback;
import com.example.policyfolio.ViewModels.NavigationViewModels.AddViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPolicyDetailsFragment extends Fragment implements BasicDropdownTextAdapter.ParentCallback {

    private View rootView;
    private AddViewModel viewModel;

    private TextView insuranceProvider;
    private TextView policyNumber;
    private EditText coverAmount;

    private FrameLayout premiumFrame;
    private TextView premiumText;
    private RecyclerView premiumChoice;
    private BasicDropdownTextAdapter premiumAdapter;
    private String[] premiums;

    private EditText premiumAmount;

    private LinearLayout datePicker;
    private TextView date;
    private ImageView dateImage;
    private Long dateEpoch;

    private FrameLayout nomineeFrame;
    private TextView nomineeText;
    private RecyclerView nomineeChoice;
    private ArrayList<Nominee> nominees;
    private BasicDropdownNomineeAdapter nomineeAdapter;

    private FrameLayout addPolicy;
    private TextView documentAddText;
    private ImageView documentAddImage;
    private TextView optional1;

    private FrameLayout clickPolicy;
    private TextView optional2;



    private AddPolicyCallback callback;

    public AddPolicyDetailsFragment() {
        // Required empty public constructor
    }

    public AddPolicyDetailsFragment(AddPolicyCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_policy_details, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(AddViewModel.class);

        insuranceProvider = rootView.findViewById(R.id.insurance_provider);
        policyNumber = rootView.findViewById(R.id.policy_number);
        coverAmount = rootView.findViewById(R.id.cover_amount);

        premiumFrame = rootView.findViewById(R.id.frequency_frame);
        premiumText = rootView.findViewById(R.id.frequency_text);
        premiumChoice = rootView.findViewById(R.id.frequency);
        premiums = getResources().getStringArray(R.array.premium_frequency);

        premiumAmount = rootView.findViewById(R.id.premium_amount);

        datePicker = rootView.findViewById(R.id.date_picker);
        date = rootView.findViewById(R.id.date);
        dateImage = rootView.findViewById(R.id.date_image);

        nomineeFrame = rootView.findViewById(R.id.nominee_frame);
        nomineeText = rootView.findViewById(R.id.nominee_text);
        nomineeChoice = rootView.findViewById(R.id.nominee);
        nominees = new ArrayList<>();

        addPolicy = rootView.findViewById(R.id.add_document);
        documentAddText = rootView.findViewById(R.id.add_document_text);
        documentAddImage = rootView.findViewById(R.id.document_add_image);
        optional1 = rootView.findViewById(R.id.optional1);

        clickPolicy = rootView.findViewById(R.id.click_document);
        optional2 = rootView.findViewById(R.id.optional2);

        setDefaults();
        setAdapters();

        return rootView;
    }

    private void setAdapters() {
        coverAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length()>0){
                    coverAmount.setPadding(0,4,0,4);
                    coverAmount.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                else {
                    coverAmount.setPadding(24,4,32,4);
                    coverAmount.setBackground(getResources().getDrawable(R.drawable.text_background_8dp));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        premiumAdapter = new BasicDropdownTextAdapter(getContext(),premiums, this);
        premiumChoice.setAdapter(premiumAdapter);
        premiumChoice.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

        premiumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                premiumText.setVisibility(View.GONE);
                premiumChoice.setVisibility(View.VISIBLE);
            }
        });

        premiumAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length()>0){
                    premiumAmount.setPadding(0,4,0,4);
                    premiumAmount.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                else {
                    premiumAmount.setPadding(24,4,32,4);
                    premiumAmount.setBackground(getResources().getDrawable(R.drawable.text_background_8dp));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setDefaults() {
        insuranceProvider.setText(viewModel.getProvider().getName());
        policyNumber.setText(viewModel.getPolicyNumber());
    }

    @Override
    public void setValue(int position, int type) {
        switch (type){
            case Constants.DropDownType.PREMIUM_FREQUENCY:
                premiumText.setText(premiums[position]);
                premiumText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                premiumText.setPadding(0,4,0,4);
                premiumChoice.setVisibility(View.GONE);
                premiumFrame.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                viewModel.setPremiumFrequency(position);
                break;
        }
    }
}
