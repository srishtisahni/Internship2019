package com.example.policyfolio.UI.Fragments;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.policyfolio.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.EmailPhoneCallback;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPhoneFragment extends Fragment {

    private View rootView;
    private LoginSignUpViewModel viewModel;
    private EmailPhoneCallback callback;

    private EditText email;
    private EditText phone;
    private Button next;

    private TextView textError;

    public EmailPhoneFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public EmailPhoneFragment(EmailPhoneCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_email_phone, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(LoginSignUpViewModel.class);

        Bundle bundle = getArguments();

        email = rootView.findViewById(R.id.email);
        phone = rootView.findViewById(R.id.phone);
        next = rootView.findViewById(R.id.next);

        if(viewModel.getEmail()!=null){
            email.setText(viewModel.getEmail());
        }

        if(bundle.getInt(Constants.Login.TYPE,-1) == Constants.Login.Type.PHONE) {
            email.setVisibility(View.GONE);
            next.setText("Sign Up");
            phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        }
        else if(bundle.getInt(Constants.Login.TYPE, -1) == Constants.Login.Type.EMAIL) {
            phone.setVisibility(View.GONE);
            next.setText("Next");
        }

        textError = rootView.findViewById(R.id.text_empty);

        setOnClick();

        return rootView;
    }

    private void setOnClick() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getVisibility()!=View.GONE) {
                    String text = email.getText().toString();
                    if (text.equals("")) {
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Empty Field Not Allowed");
                    }
                    else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Invalid Email");
                    }
                    else {
                        textError.setVisibility(View.GONE);
                        viewModel.setEmail(text);
                        callback.EmailSignUp();
                    }
                }
                else if(phone.getVisibility() != View.GONE){
                    String text = phone.getText().toString();
                    Log.e("TEXT",text);
                    if (text.equals("")) {
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Empty Field Not Allowed");
                    }
                    else if(!Patterns.PHONE.matcher(text).matches()){
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Invalid PhoneNumber");
                    }
                    else {
                        textError.setVisibility(View.GONE);
                        viewModel.setPhone(text);
                        callback.PhoneSignUp();
                    }
                }
            }
        });
    }

}
