package com.example.policyfolio.ui.login;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.util.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.viewmodels.LoginSignUpViewModel;
import com.hbb20.CountryCodePicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPhoneFragment extends Fragment {

    private View rootView;
    private LoginSignUpViewModel viewModel;
    private LoginCallback callback;

    private EditText email;
    private LinearLayout phone;
    private EditText phoneText;
    private CountryCodePicker ccp;
    private Button done;

    private TextView textError;

    public EmailPhoneFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public EmailPhoneFragment(LoginCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_email_phone, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(LoginSignUpViewModel.class);
        viewModel.initiateRepo(getContext());

        Bundle bundle = getArguments();

        email = rootView.findViewById(R.id.email);
        phoneText = rootView.findViewById(R.id.phone_text);
        phone = rootView.findViewById(R.id.phone);
        ccp = rootView.findViewById(R.id.ccp);
        done = rootView.findViewById(R.id.done);

        if(viewModel.getEmail()!=null){
            email.setText(viewModel.getEmail());
        }

        if(bundle.getInt(Constants.LoginInInfo.TYPE,-1) == Constants.LoginInInfo.Type.PHONE) {
            email.setVisibility(View.GONE);
            ccp.registerCarrierNumberEditText(phoneText);
            ccp.setNumberAutoFormattingEnabled(true);
            done.setText("Sign Up");
        }
        else if(bundle.getInt(Constants.LoginInInfo.TYPE, -1) == Constants.LoginInInfo.Type.EMAIL) {
            phone.setVisibility(View.GONE);
            done.setText("Next");
        }

        textError = rootView.findViewById(R.id.text_empty);

        setOnClick();

        return rootView;
    }


    private void setOnClick() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(getActivity());
                if(email.getVisibility()!=View.GONE) {
                    String email = EmailPhoneFragment.this.email.getText().toString();
                    if (email.equals("")) {
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Empty Field Not Allowed");
                    }
                    else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Invalid Email Id");
                    }
                    else {
                        textError.setVisibility(View.GONE);
                        viewModel.setEmail(email);
                        callback.EmailNext();
                    }
                }
                else if(phoneText.getVisibility() != View.GONE){
                    String phone = ccp.getFormattedFullNumber();
                    if (phone.equals("")) {
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Empty Field Not Allowed");
                    }
                    else if(!ccp.isValidFullNumber()){
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Invalid PhoneNumber");
                    }
                    else {
                        textError.setVisibility(View.GONE);
                        viewModel.setPhone(phone);
                        callback.PhoneSignUp();
                    }
                }
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}
