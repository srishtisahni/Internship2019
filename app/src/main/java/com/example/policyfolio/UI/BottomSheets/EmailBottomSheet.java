package com.example.policyfolio.UI.BottomSheets;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailBottomSheet extends Fragment {

    private View rootView;

    private EditText email;
    private Button next;
    private TextView error;

    private EmailSheetCallback callBack;

    public EmailBottomSheet() {
    }

    @SuppressLint("ValidFragment")
    public EmailBottomSheet(EmailSheetCallback callBack){
        this.callBack = callBack;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.bottom_sheet_email, container, false);

        email = rootView.findViewById(R.id.email);
        next = rootView.findViewById(R.id.next);
        error = rootView.findViewById(R.id.error);

        if(getArguments()!=null){
            email.setText(getArguments().getString(Constants.User.EMAIL,null));
        }

        next.setEnabled(false);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if (text.equals("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                    next.setEnabled(false);
                    next.setBackground(getResources().getDrawable(R.drawable.disabled_button_background));
                    next.setTextColor(getResources().getColor(R.color.Grey));
                    error.setVisibility(View.VISIBLE);
                }
                else {
                    next.setEnabled(true);
                    next.setBackground(getResources().getDrawable(R.drawable.button_background_green));
                    next.setTextColor(getResources().getColor(R.color.white));
                    error.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                callBack.ForgotPassword(email.getText().toString());
            }
        });

        return rootView;
    }

}
