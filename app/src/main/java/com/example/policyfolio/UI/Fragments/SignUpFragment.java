package com.example.policyfolio.UI.Fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.policyfolio.Constants;
import com.example.policyfolio.Data.Facebook;
import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private View rootView;
    private LoginSignUpViewModel viewModel;

    private EditText name;
    private EditText birthday;
    private Spinner gender;
    private EditText city;
    private EditText password;

    private TextView nameError;
    private TextView birthdayError;
    private TextView cityError;
    private TextView passwordError;

    private ArrayAdapter<CharSequence> genderAdapter;
    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(LoginSignUpViewModel.class);

        name = rootView.findViewById(R.id.name);
        birthday = rootView.findViewById(R.id.birthday);
        gender = rootView.findViewById(R.id.gender);
        city = rootView.findViewById(R.id.city);
        password = rootView.findViewById(R.id.password);

        nameError = rootView.findViewById(R.id.name_empty);
        birthdayError = rootView.findViewById(R.id.birthday_empty);
        cityError = rootView.findViewById(R.id.city_empty);
        passwordError = rootView.findViewById(R.id.password_empty);

        setGenderAdapter();

        return rootView;
    }

    private void setGenderAdapter() {
        genderAdapter =  ArrayAdapter.createFromResource(getContext(), R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.custom_spinner_layout);
        gender.setAdapter(genderAdapter);

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender.setSelection(i);
                if(i!=0)
                    ((TextView) gender.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                else
                    ((TextView) gender.getChildAt(0)).setTextColor(getResources().getColor(R.color.borderGrey));
                ((TextView) gender.getChildAt(0)).setGravity(Gravity.CENTER);
                ((TextView) gender.getChildAt(0)).setTextSize(16);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                gender.setSelection(0);
                ((TextView) gender.getChildAt(0)).setTextColor(getResources().getColor(R.color.borderGrey));
                ((TextView) gender.getChildAt(0)).setGravity(Gravity.CENTER);
                ((TextView) gender.getChildAt(0)).setTextSize(16);
            }
        });
    }

}
