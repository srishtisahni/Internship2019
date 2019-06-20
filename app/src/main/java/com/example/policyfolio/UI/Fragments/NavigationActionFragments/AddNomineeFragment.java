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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.Adapters.BasicDropdownTextAdapter;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.NomineeCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.NavigationViewModels.NomineeViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNomineeFragment extends Fragment implements BasicDropdownTextAdapter.ParentCallback {

    private View rootView;
    private NomineeViewModel viewModel;

    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText alternativePhone;

    private TextView relation;
    private RecyclerView relationChoice;
    private BasicDropdownTextAdapter textAdapter;
    private String[] relations;

    private Button done;

    private NomineeCallback callback;

    public AddNomineeFragment() {
        // Required empty public constructor
    }

    public AddNomineeFragment(NomineeCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_nominee, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(NomineeViewModel.class);

        name = rootView.findViewById(R.id.name);
        email = rootView.findViewById(R.id.email);
        phone = rootView.findViewById(R.id.phone);
        alternativePhone = rootView.findViewById(R.id.alt_phone);

        relation = rootView.findViewById(R.id.relation);
        relationChoice = rootView.findViewById(R.id.relation_choice);

        relations = getResources().getStringArray(R.array.relationship_array);
        textAdapter = new BasicDropdownTextAdapter(getContext(),relations,this, Constants.DropDownType.RELATIONSHIPS);
        relationChoice.setAdapter(textAdapter);
        relationChoice.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

        done = rootView.findViewById(R.id.done);

        setListeners();

        return rootView;
    }

    private void setListeners() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if(string.length()>0)
                    name.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                else
                    name.setBackground(getResources().getDrawable(R.drawable.text_background_8dp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if(string.length()>0)
                    email.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                else
                    email.setBackground(getResources().getDrawable(R.drawable.text_background_8dp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if(string.length()>0)
                    phone.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                else
                    phone.setBackground(getResources().getDrawable(R.drawable.text_background_8dp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alternativePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if(string.length()>0)
                    alternativePhone.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                else
                    alternativePhone.setBackground(getResources().getDrawable(R.drawable.text_background_8dp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        relation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relation.setVisibility(View.GONE);
                relationChoice.setVisibility(View.VISIBLE);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = AddNomineeFragment.this.email.toString();
                String name = AddNomineeFragment.this.name.toString();
                String phone = AddNomineeFragment.this.phone.toString();
                String alternativePhone = AddNomineeFragment.this.alternativePhone.toString();

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    Toast.makeText(getContext(),"Enter a valid email address",Toast.LENGTH_LONG).show();

                if(email.length()>0 && phone.length()>0 && name.length()>0 && relation.getCurrentTextColor()==getResources().getColor(R.color.colorPrimaryDark)){
                    viewModel.setEmail(email);
                    viewModel.setAlternateNumber(alternativePhone);
                    viewModel.setName(name);
                    viewModel.setPhone(phone);
                    callback.done();
                }
                else {
                    Toast.makeText(getContext(),"Incomplete Information",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void setValue(int position, int type) {
        if(type == Constants.DropDownType.RELATIONSHIPS){
            relation.setText(relations[position]);
            relation.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            relation.setVisibility(View.VISIBLE);
            relationChoice.setVisibility(View.GONE);
            viewModel.setRelation(position);
        }
    }
}
