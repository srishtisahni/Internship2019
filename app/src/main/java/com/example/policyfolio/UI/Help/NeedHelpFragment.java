package com.example.policyfolio.UI.Help;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.policyfolio.R;
import com.example.policyfolio.UI.Adapters.ListAdapters.BasicDropdownTextAdapter;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.HelpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NeedHelpFragment extends Fragment implements BasicDropdownTextAdapter.ParentCallback {

    private View rootView;
    private HelpViewModel viewModel;

    private LinearLayout type;
    private TextView textType;
    private RecyclerView typeChoice;
    private BasicDropdownTextAdapter typeAdapter;
    private String[] queryTypes;

    private EditText query;
    private Button submit;
    private TextView wordCount;

    private NeedHelpCallback callback;


    public NeedHelpFragment() {
        // Required empty public constructor
    }

    public NeedHelpFragment(NeedHelpCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_need_help, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(HelpViewModel.class);
        viewModel.initiateRepo(getContext());

        type = rootView.findViewById(R.id.type);
        textType = rootView.findViewById(R.id.type_text);
        typeChoice = rootView.findViewById(R.id.type_choice);
        queryTypes = getResources().getStringArray(R.array.help_array);

        query = rootView.findViewById(R.id.query);
        submit = rootView.findViewById(R.id.submit);
        wordCount = rootView.findViewById(R.id.word_count);

        setUpAdapterAndListeners();

        return rootView;
    }

    private void setUpAdapterAndListeners() {
        if(viewModel.getType() != -1)
            textType.setText(queryTypes[viewModel.getType()]);
        typeAdapter = new BasicDropdownTextAdapter(getContext(),queryTypes,this, Constants.DropDownType.QUERY);
        typeChoice.setAdapter(typeAdapter);
        typeChoice.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeChoice.setVisibility(View.VISIBLE);
                textType.setVisibility(View.GONE);
            }
        });

        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()<50)
                    wordCount.setTextColor(getResources().getColor(R.color.red));
                else
                    wordCount.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                wordCount.setText("" + s.toString().length());
                if(s.toString().length() == 0)
                    wordCount.setVisibility(View.GONE);
                else wordCount.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.getType() == -1){
                    Toast.makeText(getContext(),"Choose a valid Category",Toast.LENGTH_LONG).show();
                }
                else if(query.getText().toString().length() < 50){
                    Toast.makeText(getContext(),"The query should be atleast 50 characters long",Toast.LENGTH_LONG).show();
                }
                else {
                    viewModel.setQuery(query.getText().toString());
                    submit.setEnabled(false);
                    callback.save();
                }
            }
        });
    }

    @Override
    public void setValue(int position, int type) {
        if(type == Constants.DropDownType.QUERY){
            typeChoice.setVisibility(View.GONE);
            textType.setVisibility(View.VISIBLE);
            textType.setText(queryTypes[position]);
            viewModel.setType(position);
        }
    }

    public void enableButton() {
        submit.setEnabled(true);
    }
}
