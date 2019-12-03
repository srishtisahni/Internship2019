package com.example.policyfolio.ui.bottomsheets;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.policyfolio.R;
import com.example.policyfolio.viewmodels.LoginSignUpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class OTPBottomSheet extends Fragment {

    private View rootView;
    private LoginSignUpViewModel viewModel;

    private EditText otp;
    private TextView error;
    private Button next;
    private TextView text;

    private OTPSheetCallback callback;


    public OTPBottomSheet() {
        // Required empty public constructor
    }

    public OTPBottomSheet(OTPSheetCallback callback, LoginSignUpViewModel viewModel){
        this.callback = callback;
        this.viewModel = viewModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bottom_sheet_otp, container, false);

        otp = rootView.findViewById(R.id.otp);
        next = rootView.findViewById(R.id.next);
        error = rootView.findViewById(R.id.error);
        text = rootView.findViewById(R.id.text);

        text.setText("Enter the OTP sent to " + viewModel.getPhone());


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getText().toString().length() == 0){
                    error.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    error.setTextColor(getResources().getColor(android.R.color.transparent));
                    callback.done(otp.getText().toString());
                }
            }
        });

        return rootView;
    }

    public void setOTP(String message) {
        if(otp!=null && message!=null){
            otp.setText(message);
        }
    }
}
