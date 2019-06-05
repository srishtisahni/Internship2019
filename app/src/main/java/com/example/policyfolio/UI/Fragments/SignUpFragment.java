package com.example.policyfolio.UI.Fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.SignUpFragmentCallback;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private View rootView;
    private LoginSignUpViewModel viewModel;

    private EditText name;
    private TextView birthday;
    private Spinner gender;
    private EditText city;
    private EditText password;
    private Button signUp;

    private TextView nameError;
    private TextView birthdayError;
    private TextView cityError;
    private TextView passwordError;

    private ArrayAdapter<CharSequence> genderAdapter;
    private Long birthdayEpoch;
    private int genderSelection;

    private SignUpFragmentCallback callback;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SignUpFragment(SignUpFragmentCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(LoginSignUpViewModel.class);
        viewModel.initiateRepo(getContext());

        name = rootView.findViewById(R.id.name);
        birthday = rootView.findViewById(R.id.birthday);
        gender = rootView.findViewById(R.id.gender);
        city = rootView.findViewById(R.id.city);
        password = rootView.findViewById(R.id.password);
        signUp = rootView.findViewById(R.id.sign_up);

        nameError = rootView.findViewById(R.id.name_empty);
        birthdayError = rootView.findViewById(R.id.birthday_empty);
        cityError = rootView.findViewById(R.id.city_empty);
        passwordError = rootView.findViewById(R.id.password_empty);

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchDate();
            }
        });

        setGenderAdapter();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });

        return rootView;
    }

    private void SignUp() {
        String name = this.name.getText().toString();
        if(name.equals("")){
            nameError.setVisibility(View.VISIBLE);
        }
        else {
            nameError.setVisibility(View.GONE);
            viewModel.setName(name);
        }

        if(birthdayEpoch!=null){
            birthdayError.setVisibility(View.GONE);
            viewModel.setBirthDay(birthdayEpoch);
        }
        else {
            birthdayError.setVisibility(View.VISIBLE);
        }

        viewModel.setGender(genderSelection);

        String city = this.city.getText().toString();
        if(city.equals("")){
            cityError.setVisibility(View.VISIBLE);
        }
        else {
            cityError.setVisibility(View.GONE);
            viewModel.setCity(city);
        }

        String password = this.password.getText().toString();
        if(password.length()<8){
            passwordError.setText("Password should be atleast 8 characters long");
            passwordError.setVisibility(View.VISIBLE);
        }
        else {
            passwordError.setVisibility(View.GONE);
            viewModel.setPassword(password);
        }

        if(nameError.getVisibility() == View.GONE && birthdayError.getVisibility() == View.GONE && cityError.getVisibility() == View.GONE && passwordError.getVisibility() == View.GONE)
            callback.SignUp();
    }

    private void FetchDate() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.date_picker);
        dialog.setTitle("");
        DatePicker datePicker = dialog.findViewById(R.id.date_picker);
        final Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year,monthOfYear,dayOfMonth);
                birthdayEpoch = calendar.getTimeInMillis();
                birthday.setText(Constants.DATE_FORMAT.format(birthdayEpoch));
                birthday.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        dialog.show();
    }

    private void setGenderAdapter() {
        genderAdapter =  ArrayAdapter.createFromResource(getContext(), R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.custom_spinner_layout);
        gender.setAdapter(genderAdapter);

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender.setSelection(i);
                genderSelection = i;
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
                genderSelection = 0;
                ((TextView) gender.getChildAt(0)).setTextColor(getResources().getColor(R.color.borderGrey));
                ((TextView) gender.getChildAt(0)).setGravity(Gravity.CENTER);
                ((TextView) gender.getChildAt(0)).setTextSize(16);
            }
        });
    }

}
