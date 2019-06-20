package com.example.policyfolio.UI.Fragments.NavigationActionFragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.R;
import com.example.policyfolio.Util.Adapters.BasicDropdownNomineeAdapter;
import com.example.policyfolio.Util.Adapters.BasicDropdownTextAdapter;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.AddPolicyCallback;
import com.example.policyfolio.ViewModels.NavigationViewModels.AddViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPolicyDetailsFragment extends Fragment implements BasicDropdownTextAdapter.ParentCallback, BasicDropdownNomineeAdapter.ParentCallback {

    private View rootView;
    private AddViewModel viewModel;

    private TextView insuranceProvider;
    private TextView policyNumber;
    private EditText coverAmount;

    private FrameLayout premiumFrame;
    private TextView premiumText;
    private RecyclerView premiumChoice;
    private BasicDropdownTextAdapter premiumAdapter;
    private String[] premiums;

    private EditText premiumAmount;

    private LinearLayout premiumDatePickerLayout;
    private TextView premiumDate;
    private ImageView premiumDateImage;
    private Long premiumDateEpoch;

    private LinearLayout matureDatePickerLayout;
    private TextView matureDate;
    private ImageView matureDateImage;
    private Long matureDateEpoch;


    private FrameLayout nomineeFrame;
    private TextView nomineeText;
    private RecyclerView nomineeChoice;
    private ArrayList<Nominee> nominees;
    private BasicDropdownNomineeAdapter nomineeAdapter;

    private LinearLayout addPolicy;
    private TextView documentAddText;
    private ImageView documentAddImage;
    private TextView optional1;

    private LinearLayout clickPolicy;
    private TextView optional2;

    private Button done;

    private AddPolicyCallback callback;

    public AddPolicyDetailsFragment() {
        // Required empty public constructor
    }

    public AddPolicyDetailsFragment(AddPolicyCallback callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_policy_details, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(AddViewModel.class);
        viewModel.initiateRepo(getContext());

        insuranceProvider = rootView.findViewById(R.id.insurance_provider);
        policyNumber = rootView.findViewById(R.id.policy_number);
        coverAmount = rootView.findViewById(R.id.cover_amount);

        premiumFrame = rootView.findViewById(R.id.frequency_frame);
        premiumText = rootView.findViewById(R.id.frequency_text);
        premiumChoice = rootView.findViewById(R.id.frequency);
        premiums = getResources().getStringArray(R.array.premium_frequency);

        premiumAmount = rootView.findViewById(R.id.premium_amount);

        premiumDatePickerLayout = rootView.findViewById(R.id.date_picker);
        premiumDate = rootView.findViewById(R.id.date);
        premiumDateImage = rootView.findViewById(R.id.date_image);

        matureDatePickerLayout = rootView.findViewById(R.id.date_picker2);
        matureDate = rootView.findViewById(R.id.date2);
        matureDateImage = rootView.findViewById(R.id.date_image2);

        nomineeFrame = rootView.findViewById(R.id.nominee_frame);
        nomineeText = rootView.findViewById(R.id.nominee_text);
        nomineeChoice = rootView.findViewById(R.id.nominee);
        nominees = new ArrayList<>();

        addPolicy = rootView.findViewById(R.id.add_document);
        documentAddText = rootView.findViewById(R.id.add_document_text);
        documentAddImage = rootView.findViewById(R.id.document_add_image);
        optional1 = rootView.findViewById(R.id.optional1);

        clickPolicy = rootView.findViewById(R.id.click_document);
        optional2 = rootView.findViewById(R.id.optional2);

        done = rootView.findViewById(R.id.done);

        setAdapters();
        setDefaults();

        return rootView;
    }

    private void setAdapters() {
        coverAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length()>0){
                    coverAmount.setPadding(0,4,0,4);
                    coverAmount.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                else {
                    coverAmount.setPadding(24,4,32,4);
                    coverAmount.setBackground(getResources().getDrawable(R.drawable.text_background_8dp));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        premiumAdapter = new BasicDropdownTextAdapter(getContext(),premiums, this,Constants.DropDownType.PREMIUM_FREQUENCY);
        premiumChoice.setAdapter(premiumAdapter);
        premiumChoice.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

        premiumFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverAmount.clearFocus();
                premiumAmount.clearFocus();

                premiumFrame.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                premiumFrame.setPadding(0,4,0,4);
                premiumText.setVisibility(View.GONE);
                premiumChoice.setVisibility(View.VISIBLE);
            }
        });

        premiumAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length()>0){
                    premiumAmount.setPadding(0,4,0,4);
                    premiumAmount.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                else {
                    premiumAmount.setPadding(24,4,32,4);
                    premiumAmount.setBackground(getResources().getDrawable(R.drawable.text_background_8dp));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        premiumDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverAmount.clearFocus();
                premiumAmount.clearFocus();

                final Dialog dialog = new Dialog(getContext(),R.style.DialogTheme);
                dialog.setContentView(R.layout.date_picker);
                dialog.setTitle("");
                DatePicker datePicker = dialog.findViewById(R.id.date_picker);
                final Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(premiumDateEpoch*1000);
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year,monthOfYear,dayOfMonth);
                        premiumDateEpoch = (calendar.getTimeInMillis())/1000;
                        viewModel.setPremiumDateEpoch(premiumDateEpoch);
                        premiumDate.setText(Constants.Time.DATE_FORMAT.format(premiumDateEpoch*1000));
                        premiumDate.setPadding(0,4,0,4);
                        premiumDate.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        premiumDateImage.setVisibility(View.GONE);
                        premiumDatePickerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                });
                dialog.show();
            }
        });

        matureDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverAmount.clearFocus();
                premiumAmount.clearFocus();


                final Dialog dialog = new Dialog(getContext(),R.style.DialogTheme);
                dialog.setContentView(R.layout.date_picker);
                dialog.setTitle("");
                DatePicker datePicker = dialog.findViewById(R.id.date_picker);
                final Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(matureDateEpoch*1000);
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year,monthOfYear,dayOfMonth);
                        matureDateEpoch = (calendar.getTimeInMillis())/1000;
                        viewModel.setMatureDateEpoch(matureDateEpoch);
                        matureDate.setText(Constants.Time.DATE_FORMAT.format(matureDateEpoch*1000));
                        matureDate.setPadding(0,4,0,4);
                        matureDate.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        matureDateImage.setVisibility(View.GONE);
                        matureDatePickerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                });
                dialog.show();
            }
        });

        nomineeAdapter = new BasicDropdownNomineeAdapter(getContext(),nominees,this);
        nomineeChoice.setAdapter(nomineeAdapter);
        nomineeChoice.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

        nomineeFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverAmount.clearFocus();
                premiumAmount.clearFocus();

                if(nominees.size()!=0) {
                    nomineeFrame.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    nomineeText.setVisibility(View.GONE);
                    nomineeChoice.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(getContext(),"No Nominees exist for the User",Toast.LENGTH_LONG).show();
            }
        });

        addPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverAmount.clearFocus();
                premiumAmount.clearFocus();

                callback.addPolicyImage();
            }
        });

        clickPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverAmount.clearFocus();
                premiumAmount.clearFocus();

                callback.clickPolicyImage();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverAmount.clearFocus();
                premiumAmount.clearFocus();

                if(!(premiumAmount.getText().toString().equals("") || premiumDateEpoch == null || premiumText.getVisibility()==View.GONE || premiumText.getCurrentTextColor()!=getResources().getColor(R.color.colorPrimaryDark))) {
                    viewModel.setPremiumAmount(premiumAmount.getText().toString());
                    viewModel.setCoverAmount(coverAmount.getText().toString());
                    callback.done();
                }
                else
                    Toast.makeText(getContext(),"Information Incomplete",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDefaults() {
        insuranceProvider.setText(viewModel.getProvider().getName());
        policyNumber.setText(viewModel.getPolicyNumber());
        viewModel.fetchNominees().observe(this, new Observer<List<Nominee>>() {
            @Override
            public void onChanged(List<Nominee> nominees) {
                AddPolicyDetailsFragment.this.nominees.clear();
                AddPolicyDetailsFragment.this.nominees.addAll(nominees);
                nomineeAdapter.notifyDataSetChanged();
            }
        });
        premiumDateEpoch = System.currentTimeMillis()/1000;
        matureDateEpoch = System.currentTimeMillis()/1000;
    }

    @Override
    public void setValue(int position, int type) {
        coverAmount.clearFocus();
        premiumAmount.clearFocus();
        switch (type){
            case Constants.DropDownType.PREMIUM_FREQUENCY:
                premiumText.setText(premiums[position]);
                premiumText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                premiumText.setPadding(0,4,0,4);
                premiumChoice.setVisibility(View.GONE);
                premiumText.setVisibility(View.VISIBLE);
                viewModel.setPremiumFrequency(position);
                break;
            case Constants.DropDownType.NOMINEE:
                String[] relations = getResources().getStringArray(R.array.relationship_array);
                nomineeText.setText(nominees.get(position).getName() + ", " + relations[nominees.get(position).getRelation()]);
                nomineeText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                nomineeText.setPadding(0,4,0,4);
                nomineeChoice.setVisibility(View.GONE);
                nomineeText.setVisibility(View.VISIBLE);
                viewModel.setNominee(nominees.get(position));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.PermissionAndRequests.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data!=null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
            Log.e("BITMAP",bmp+"");
            documentAddImage.setVisibility(View.GONE);
            documentAddText.setVisibility(View.GONE);
            clickPolicy.setVisibility(View.GONE);
            optional2.setVisibility(View.GONE);
            optional1.setText("Policy Document");
            viewModel.saveImage(bmp).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                   if (s != null)
                       viewModel.setPhotoUrl(s);
                   else
                       Toast.makeText(getContext(), "Error uploading Image", Toast.LENGTH_LONG).show();
                }
            });
        }
        if(requestCode == Constants.PermissionAndRequests.CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data!=null){
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            documentAddImage.setVisibility(View.GONE);
            documentAddText.setVisibility(View.GONE);
            clickPolicy.setVisibility(View.GONE);
            optional2.setVisibility(View.GONE);
            optional1.setText("Policy Document");
                viewModel.saveImage(bmp).observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (s != null)
                            viewModel.setPhotoUrl(s);
                        else
                            Toast.makeText(getContext(), "Error uploading Image", Toast.LENGTH_LONG).show();
                    }
                });
        }
    }
}
