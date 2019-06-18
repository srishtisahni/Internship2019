package com.example.policyfolio.UI.PopUps;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.policyfolio.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OTPPopUp extends Fragment {


    public OTPPopUp() {
        // Required empty public constructor
    }
    //TODO Enter OTP

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pop_up_otp, container, false);
    }

}
