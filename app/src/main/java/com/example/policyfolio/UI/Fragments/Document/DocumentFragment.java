package com.example.policyfolio.UI.Fragments.Document;


import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.policyfolio.R;
import com.example.policyfolio.Repo.Database.DataClasses.Documents;
import com.example.policyfolio.Util.CallBackListeners.DocumentCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.DocumentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentFragment extends Fragment {

    private View rootView;
    private DocumentViewModel viewModel;

    private LinearLayout uploadAdhaar;
    private CardView uploadedAdhaar;

    private LinearLayout uploadPassport;
    private CardView uploadedPassport;

    private LinearLayout uploadPAN;
    private CardView uploadedPAN;

    private LinearLayout uploadVoterId;
    private CardView uploadedVoterId;

    private LinearLayout uploadDrivingLicense;
    private CardView uploadedDrivingLicense;
    
    private LinearLayout uploadRationCard;
    private CardView uploadedRationCard;

    private DocumentCallback callback;

    public DocumentFragment() {
        // Required empty public constructor
    }

    public DocumentFragment(DocumentCallback callback){
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_document, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(DocumentViewModel.class);
        viewModel.initiateRepo(getContext());
        
        uploadAdhaar = rootView.findViewById(R.id.upload_adhaar);
        uploadedAdhaar = rootView.findViewById(R.id.adhaar_card);

        uploadPassport = rootView.findViewById(R.id.upload_passport);
        uploadedPassport = rootView.findViewById(R.id.passport_card);

        uploadPAN = rootView.findViewById(R.id.upload_pan);
        uploadedPAN = rootView.findViewById(R.id.pan_card);

        uploadVoterId = rootView.findViewById(R.id.upload_voter);
        uploadedVoterId = rootView.findViewById(R.id.voter_card);

        uploadDrivingLicense = rootView.findViewById(R.id.upload_driving);
        uploadedDrivingLicense = rootView.findViewById(R.id.driving_card);

        uploadRationCard = rootView.findViewById(R.id.upload_ration);
        uploadedRationCard = rootView.findViewById(R.id.ration_card);
        
        checkUpload();
        setUpListeners();

        return rootView;
    }

    private void setUpListeners() {
        uploadAdhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.uploadDocument(Constants.Documents.ADHAAR);
            }
        });
        uploadedAdhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.viewUploadedDocument(Constants.Documents.ADHAAR);
            }
        });

        uploadPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.uploadDocument(Constants.Documents.PASSPORT);
            }
        });
        uploadedPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.viewUploadedDocument(Constants.Documents.PASSPORT);
            }
        });

        uploadPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.uploadDocument(Constants.Documents.PAN);
            }
        });
        uploadedPAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.viewUploadedDocument(Constants.Documents.PAN);
            }
        });

        uploadVoterId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.uploadDocument(Constants.Documents.VOTER_ID);
            }
        });
        uploadedVoterId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.viewUploadedDocument(Constants.Documents.VOTER_ID);
            }
        });

        uploadDrivingLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.uploadDocument(Constants.Documents.DRIVING_LICENSE);
            }
        });
        uploadedDrivingLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.viewUploadedDocument(Constants.Documents.DRIVING_LICENSE);
            }
        });

        uploadRationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.uploadDocument(Constants.Documents.RATION_CARD);
            }
        });
        uploadedRationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.viewUploadedDocument(Constants.Documents.RATION_CARD);
            }
        });
    }

    private void checkUpload() {
        viewModel.fetchDocument().observe(this, new Observer<Documents>() {
            @Override
            public void onChanged(Documents documents) {
                if(documents!=null) {
                    if (documents.getAdhaarLink() != null) {
                        uploadAdhaar.setVisibility(View.GONE);
                        uploadedAdhaar.setVisibility(View.VISIBLE);
                    } else {
                        uploadAdhaar.setVisibility(View.VISIBLE);
                        uploadedAdhaar.setVisibility(View.GONE);
                    }

                    if (documents.getPassportLink() != null) {
                        uploadPassport.setVisibility(View.GONE);
                        uploadedPassport.setVisibility(View.VISIBLE);
                    } else {
                        uploadPassport.setVisibility(View.VISIBLE);
                        uploadedPassport.setVisibility(View.GONE);
                    }

                    if (documents.getPanLink() != null) {
                        uploadPAN.setVisibility(View.GONE);
                        uploadedPAN.setVisibility(View.VISIBLE);
                    } else {
                        uploadPAN.setVisibility(View.VISIBLE);
                        uploadedPAN.setVisibility(View.GONE);
                    }

                    if (documents.getVoterIdLink() != null) {
                        uploadVoterId.setVisibility(View.GONE);
                        uploadedVoterId.setVisibility(View.VISIBLE);
                    } else {
                        uploadVoterId.setVisibility(View.VISIBLE);
                        uploadedVoterId.setVisibility(View.GONE);
                    }

                    if (documents.getDrivingLicenseLink() != null) {
                        uploadDrivingLicense.setVisibility(View.GONE);
                        uploadedDrivingLicense.setVisibility(View.VISIBLE);
                    } else {
                        uploadDrivingLicense.setVisibility(View.VISIBLE);
                        uploadedDrivingLicense.setVisibility(View.GONE);
                    }

                    if (documents.getRationCardLink() != null) {
                        uploadRationCard.setVisibility(View.GONE);
                        uploadedRationCard.setVisibility(View.VISIBLE);
                    } else {
                        uploadRationCard.setVisibility(View.VISIBLE);
                        uploadedRationCard.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

}
