package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.NavigationViewModels.AddViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicAddPolicyFragment extends Fragment {

    private View rootView;
    private AddViewModel viewModel;

    private Spinner typeSpinner;
    private ArrayAdapter<CharSequence> typeAdapter;

    private Spinner providerSpinner;
    private TextView providerText;
    private FrameLayout providerFrame;

    private EditText policyNumber;

    private Button next;

    private LinearLayout divider;
    private ConstraintLayout buy;
    private RecyclerView sellerList;

    public BasicAddPolicyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_basic_add_policy, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(AddViewModel.class);

        typeSpinner = rootView.findViewById(R.id.policy_type);

        providerFrame = rootView.findViewById(R.id.frame_insurance);
        providerText = rootView.findViewById(R.id.text_insurance);
        providerSpinner = rootView.findViewById(R.id.insurance_provider);

        policyNumber = rootView.findViewById(R.id.policy_number);

        next = rootView.findViewById(R.id.next);

        divider = rootView.findViewById(R.id.divider);

        buy = rootView.findViewById(R.id.buy_new);
        sellerList = rootView.findViewById(R.id.policy_sellers);

        setTypeAdapter();

        return rootView;
    }

    private void setTypeAdapter() {
        typeAdapter = ArrayAdapter.createFromResource(getContext(),R.array.insurance_type,android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(R.layout.custom_spinner_add);
        typeSpinner.setAdapter(typeAdapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSpinner.setSelection(position);
                viewModel.setType(position);

                ((TextView) typeSpinner.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) typeSpinner.getChildAt(0)).setTextSize(16);

                providerFrame.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                buy.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                providerFrame.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                buy.setVisibility(View.GONE);

                ((TextView) typeSpinner.getChildAt(0)).setTextColor(getResources().getColor(R.color.Grey));
                ((TextView) typeSpinner.getChildAt(0)).setTextSize(16);
            }
        });
    }

}
