package com.example.policyfolio.UI.PopUps;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Patterns;
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
import com.example.policyfolio.UI.CallBackListeners.PopUpCallBack;
import com.example.policyfolio.ViewModels.PopUpViewModel;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoPopUp extends Fragment {

    private PopUpCallBack callback;
    private PopUpViewModel viewModel;
    private View rootView;

    private EditText name;
    private EditText email;
    private EditText phone;
    private TextView birthday;
    private Spinner gender;
    private EditText city;
    private Button save;

    private TextView nameError;
    private TextView emailError;
    private TextView phoneError;
    private TextView birthdayError;
    private TextView cityError;

    private Long birthdayEpoch;
    private ArrayAdapter<CharSequence> genderAdapter;
    private int genderSelection;

    public InfoPopUp() {
    }

    @SuppressLint("ValidFragment")
    public InfoPopUp(PopUpCallBack callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info_pop_up, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(PopUpViewModel.class);
        viewModel.initiateRepo(getContext());

        name = rootView.findViewById(R.id.name);
        email = rootView.findViewById(R.id.email);
        phone = rootView.findViewById(R.id.phone);
        birthday = rootView.findViewById(R.id.birthday);
        gender = rootView.findViewById(R.id.gender);
        city = rootView.findViewById(R.id.city);
        save = rootView.findViewById(R.id.save);

        nameError = rootView.findViewById(R.id.name_empty);
        emailError = rootView.findViewById(R.id.email_empty);
        phoneError = rootView.findViewById(R.id.phone_empty);
        birthdayError = rootView.findViewById(R.id.birthday_empty);
        cityError = rootView.findViewById(R.id.city_empty);

        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        setGenderAdapter();

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchDate();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        return rootView;
    }

    public void setAvailableInfo() {
        if(viewModel.getEmail()!=null)
            email.setText(viewModel.getEmail());
        if(viewModel.getName()!=null)
            name.setText(viewModel.getName());
        if(viewModel.getPhone()!= null)
            phone.setText(viewModel.getPhone());
        if (viewModel.getBirthday()!=null) {
            birthday.setText(Constants.DATE_FORMAT.format(viewModel.getBirthday()));
            birthdayEpoch = viewModel.getBirthday();
            birthday.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        gender.setSelection(viewModel.getGender());
        if(viewModel.getCity()!=null)
            city.setText(viewModel.getCity());
    }

    private void save() {
        String name = this.name.getText().toString();
        if(name.equals("")){
            nameError.setVisibility(View.VISIBLE);
        }
        else {
            nameError.setVisibility(View.GONE);
            viewModel.setName(name);
        }

        String email = this.email.getText().toString();
        if (email.equals("")) {
            emailError.setVisibility(View.VISIBLE);
            emailError.setText("The field cannot be left empty");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailError.setVisibility(View.VISIBLE);
            emailError.setText("Invalid Email Id");
        }
        else {
            emailError.setVisibility(View.GONE);
            viewModel.setEmail(email);
        }

        String phone = this.phone.getText().toString();
        if (phone.equals("")) {
            phoneError.setVisibility(View.VISIBLE);
            phoneError.setText("Empty Field Not Allowed");
        }
        else if(!Patterns.PHONE.matcher(phone).matches()){
            phoneError.setVisibility(View.VISIBLE);
            phoneError.setText("Invalid PhoneNumber");
        }
        else {
            phoneError.setVisibility(View.GONE);
            viewModel.setPhone(phone);
        }

        if(birthdayEpoch!=null){
            birthdayError.setVisibility(View.GONE);
            viewModel.setBirthday(birthdayEpoch);
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


        if(nameError.getVisibility() == View.GONE && birthdayError.getVisibility() == View.GONE && cityError.getVisibility() == View.GONE && phoneError.getVisibility() == View.GONE && emailError.getVisibility() == View.GONE)
            callback.updateInfo();
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
        genderAdapter.setDropDownViewResource(R.layout.custom_spinner_sign_up);
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
