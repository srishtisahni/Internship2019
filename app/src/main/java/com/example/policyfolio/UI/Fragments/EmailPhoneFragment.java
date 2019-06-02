package com.example.policyfolio.UI.Fragments;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private EditText emailPhone;
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

        emailPhone = rootView.findViewById(R.id.email_phone);
        next = rootView.findViewById(R.id.next);

        textError = rootView.findViewById(R.id.text_empty);

        setOnClick();

        return rootView;
    }

    private void setOnClick() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = emailPhone.getText().toString();
                if(text.equals(""))
                    textError.setVisibility(View.VISIBLE);
                else {
                    textError.setVisibility(View.GONE);
                    Integer type = viewModel.emailPhoneUpdate(text);
                    if(type == Constants.Login.Type.EMAIL)
                        callback.EmailSignUp();
                    else if(type == Constants.Login.Type.PHONE)
                        callback.PhoneSignUp();
                    else
                        Toast.makeText(getContext(),"Invalid Input",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
