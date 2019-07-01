package com.example.policyfolio.UI.BottomSheets;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.UI.Adapters.ListAdapters.BasicDropdownTextAdapter;
import com.example.policyfolio.UI.Home.HomeActivity;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.WithUser.HomeViewModel;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoBottomSheet extends Fragment implements BasicDropdownTextAdapter.ParentCallback {

    private SheetCallback callback;
    private HomeViewModel viewModel;
    private View rootView;

    private EditText name;
    private EditText email;
    private EditText phone;
    private TextView birthday;
    private TextView genderText;
    private RecyclerView genderChoice;
    private EditText city;
    private Button save;

    private TextView nameError;
    private TextView emailError;
    private TextView phoneError;
    private TextView birthdayError;
    private TextView cityError;

    private Long birthdayEpoch;
    private BasicDropdownTextAdapter genderAdapter;
    private String[] genderArray;
    private int genderSelection;

    public InfoBottomSheet() {
    }

    @SuppressLint("ValidFragment")
    public InfoBottomSheet(SheetCallback callback, HomeViewModel viewModel){
        this.callback = callback;
        this.viewModel = viewModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.pop_up_info, container, false);

        name = rootView.findViewById(R.id.name);
        email = rootView.findViewById(R.id.email);
        phone = rootView.findViewById(R.id.phone);
        birthday = rootView.findViewById(R.id.birthday);
        genderText = rootView.findViewById(R.id.gender_text);
        genderChoice = rootView.findViewById(R.id.gender_choice);
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

        setAvailableInfo();

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
            birthday.setText(Constants.Time.DATE_FORMAT.format(viewModel.getBirthday()*1000));
            birthdayEpoch = viewModel.getBirthday();
            birthday.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setValue(viewModel.getGender(),Constants.DropDownType.GENDER);
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
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.date_picker);
        dialog.setTitle("");
        DatePicker datePicker = dialog.findViewById(R.id.date_picker);
        final Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year,monthOfYear,dayOfMonth);
                birthdayEpoch = (calendar.getTimeInMillis())/1000;
                birthday.setText(Constants.Time.DATE_FORMAT.format(birthdayEpoch*1000));
                birthday.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        dialog.show();
    }

    private void setGenderAdapter() {
        genderSelection = 0;
        genderArray = getResources().getStringArray(R.array.gender_array);
        genderAdapter = new BasicDropdownTextAdapter(getContext(),genderArray,this,Constants.DropDownType.GENDER);
        genderChoice.setAdapter(genderAdapter);
        genderChoice.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        genderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderText.setVisibility(View.GONE);
                genderChoice.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void setValue(int position, int type) {
        if(type == Constants.DropDownType.GENDER){
            genderSelection = position;
            if(genderSelection == 0)
                genderText.setTextColor(getResources().getColor(R.color.Grey));
            else
                genderText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            genderText.setText(genderArray[genderSelection]);
            genderText.setVisibility(View.VISIBLE);
            genderChoice.setVisibility(View.GONE);
        }
    }

}
