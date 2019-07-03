package com.example.policyfolio.UI.Nominee;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.policyfolio.R;
import com.example.policyfolio.UI.Adapters.ListAdapters.BasicDropdownTextAdapter;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.WithUser.NomineeViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNomineeFragment extends Fragment implements BasicDropdownTextAdapter.ParentCallback {

    private View rootView;
    private NomineeViewModel viewModel;

    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText alternativePhone;

    private TextView relationText;
    private LinearLayout relation;
    private BasicDropdownTextAdapter textAdapter;
    private String[] relations;

    private Button done;

    private NomineeCallback callback;

    public AddNomineeFragment() {
        // Required empty public constructor
    }

    public AddNomineeFragment(NomineeCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_nominee, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(NomineeViewModel.class);

        name = rootView.findViewById(R.id.name);
        email = rootView.findViewById(R.id.email);
        phone = rootView.findViewById(R.id.phone);
        alternativePhone = rootView.findViewById(R.id.alt_phone);

        relationText = rootView.findViewById(R.id.relation_text);
        relation = rootView.findViewById(R.id.relation);

        relations = getResources().getStringArray(R.array.relationship_array);
        textAdapter = new BasicDropdownTextAdapter(getContext(),relations,this, Constants.ListTypes.RELATIONSHIPS, getResources().getColor(R.color.colorPrimaryDarkest));

        done = rootView.findViewById(R.id.done);

        setListeners();

        return rootView;
    }

    private void setListeners() {
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        alternativePhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if(string.length()>0)
                    name.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                else
                    name.setBackground(getResources().getDrawable(R.drawable.text_background_grey_8dp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if(string.length()>0)
                    email.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                else
                    email.setBackground(getResources().getDrawable(R.drawable.text_background_grey_8dp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if(string.length()>0)
                    phone.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                else
                    phone.setBackground(getResources().getDrawable(R.drawable.text_background_grey_8dp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alternativePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if(string.length()>0)
                    alternativePhone.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                else
                    alternativePhone.setBackground(getResources().getDrawable(R.drawable.text_background_grey_8dp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewModel.setRelation(0);
        relation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.openListSheet(Constants.ListTypes.RELATIONSHIPS,textAdapter);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString();
                String n = name.getText().toString();
                String p = phone.getText().toString();
                String aP = alternativePhone.getText().toString();

                Log.e("DETAILS",e + " " + p + " " + n + " " + aP);

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches())
                    callback.showSnackbar("Enter a valid email address");
                else if(email.length()>0 && phone.length()>0 && name.length()>0 && relationText.getCurrentTextColor()==getResources().getColor(R.color.colorPrimaryDark)){
                    viewModel.setEmail(e);
                    viewModel.setAlternateNumber(aP);
                    viewModel.setName(n);
                    viewModel.setPhone(p);
                    callback.done();
                }
                else {
                    callback.showSnackbar("Incomplete Information");
                }
            }
        });
    }

    @Override
    public void setValue(int position, int type) {
        callback.closeListSheet();
        if(type == Constants.ListTypes.RELATIONSHIPS){
            relationText.setText(relations[position]);
            relationText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            relation.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            viewModel.setRelation(position);
        }
    }
}
