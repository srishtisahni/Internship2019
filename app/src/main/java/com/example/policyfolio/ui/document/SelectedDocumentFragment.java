package com.example.policyfolio.ui.document;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.policyfolio.R;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.viewmodels.DocumentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedDocumentFragment extends Fragment {

    private View rootView;
    private DocumentViewModel viewModel;

    private TextView cardText;
    private EditText cardNumber;
    private ImageView photo;
    private TextView uploadText;
    private FloatingActionButton edit;
    private FloatingActionButton delete;
    private Button done;

    private DocumentCallback callback;
    private int type;
    private boolean updated;

    public SelectedDocumentFragment() {
        // Required empty public constructor
    }

    public SelectedDocumentFragment(DocumentCallback callback, int type, boolean uploaded) {
        this.callback = callback;
        this.type = type;
        this.updated = uploaded;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_document_selected, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(DocumentViewModel.class);
        viewModel.initiateRepo(getContext());

        cardText = rootView.findViewById(R.id.text);
        cardNumber = rootView.findViewById(R.id.card_number);
        photo = rootView.findViewById(R.id.image);
        uploadText = rootView.findViewById(R.id.text_upload);
        edit = rootView.findViewById(R.id.edit);
        delete = rootView.findViewById(R.id.delete);
        done = rootView.findViewById(R.id.done);

        setUpData();
        setUpListeners();

        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void setUpData() {
        String textUpload = "Upload File Here.";
        String cardHint = "Card Number";
        switch (type){
            case Constants.Documents.ADHAAR:
                cardText.setText(getResources().getString(R.string.adhaar_card));
                textUpload = "Upload " + getResources().getString(R.string.adhaar_card) + " Here.";
                cardHint =  getResources().getString(R.string.adhaar_card) + " Number";
                break;
            case Constants.Documents.PASSPORT:
                cardText.setText(getResources().getString(R.string.passport));
                textUpload = "Upload " + getResources().getString(R.string.passport) + " Here.";
                cardHint =  getResources().getString(R.string.passport) + " Number";
                break;
            case Constants.Documents.PAN:
                cardText.setText(getResources().getString(R.string.pan_card));
                textUpload = "Upload " + getResources().getString(R.string.pan_card) + " Here.";
                cardHint =  getResources().getString(R.string.pan_card) + " Number";
                break;
            case Constants.Documents.VOTER_ID:
                cardText.setText(getResources().getString(R.string.voter_id));
                textUpload = "Upload " + getResources().getString(R.string.voter_id) + " Here.";
                cardHint =  getResources().getString(R.string.voter_id) + " Number";
                break;
            case Constants.Documents.DRIVING_LICENSE:
                cardText.setText(getResources().getString(R.string.driving_license));
                textUpload = "Upload " + getResources().getString(R.string.driving_license) + " Here.";
                cardHint =  getResources().getString(R.string.driving_license) + " Number";
                break;
            case Constants.Documents.RATION_CARD:
                cardText.setText(getResources().getString(R.string.ration_card));
                textUpload = "Upload " + getResources().getString(R.string.ration_card) + " Here.";
                cardHint =  getResources().getString(R.string.ration_card) + " Number";
                break;
        }
        uploadText.setText(textUpload);
        cardNumber.setHint(cardHint);

        if(!updated){
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            done.setVisibility(View.GONE);
        }
        else {
            viewModel.fetchImage(type).observe(this, new Observer<Bitmap>() {
                @Override
                public void onChanged(Bitmap bitmap) {
                    if(bitmap!=null){
                        photo.setImageBitmap(bitmap);
                        uploadText.setVisibility(View.GONE);
                        photo.setOnClickListener(null);
                        photo.setScaleY(1f);
                        photo.setScaleX(1f);
                        switch (type){
                            case Constants.Documents.ADHAAR:
                                if(viewModel.getLocalCopy().getAdhaarNumber() != null)
                                    cardNumber.setText(viewModel.getLocalCopy().getAdhaarNumber());
                                break;
                            case Constants.Documents.PASSPORT:
                                if(viewModel.getLocalCopy().getPassportNumber() != null)
                                    cardNumber.setText(viewModel.getLocalCopy().getPassportNumber());
                                break;
                            case Constants.Documents.PAN:
                                if(viewModel.getLocalCopy().getPanNumber() != null)
                                    cardNumber.setText(viewModel.getLocalCopy().getPanNumber());
                                break;
                            case Constants.Documents.VOTER_ID:
                                if(viewModel.getLocalCopy().getVoterIdNumber() != null)
                                    cardNumber.setText(viewModel.getLocalCopy().getVoterIdNumber());
                                break;
                            case Constants.Documents.DRIVING_LICENSE:
                                if(viewModel.getLocalCopy().getDrivingLicenseNumber() != null)
                                    cardNumber.setText(viewModel.getLocalCopy().getDrivingLicenseNumber());
                                break;
                            case Constants.Documents.RATION_CARD:
                                if(viewModel.getLocalCopy().getRationCardNumber() != null)
                                    cardNumber.setText(viewModel.getLocalCopy().getRationCardNumber());
                                break;
                        }
                    }
                }
            });
        }
    }

    private void setUpListeners() {
        photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.getImage();
                }
            });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.setImageDrawable(getResources().getDrawable(R.drawable.upload));
                photo.setScaleX(0.5f);
                photo.setScaleY(0.5f);
                uploadText.setVisibility(View.VISIBLE);
                viewModel.setImage(null,type);
                done.setVisibility(View.VISIBLE);
                cardNumber.setText("");
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.getImage();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardNumber.getText().toString().length()>0 || updated) {
                    String card = cardNumber.getText().toString();
                    switch (type){
                        case Constants.Documents.ADHAAR:
                            viewModel.getLocalCopy().setAdhaarNumber(card);
                            break;
                        case Constants.Documents.PASSPORT:
                            viewModel.getLocalCopy().setPassportNumber(card);
                            break;
                        case Constants.Documents.PAN:
                            viewModel.getLocalCopy().setPanNumber(card);
                            break;
                        case Constants.Documents.VOTER_ID:
                            viewModel.getLocalCopy().setVoterIdNumber(card);
                            break;
                        case Constants.Documents.DRIVING_LICENSE:
                            viewModel.getLocalCopy().setDrivingLicenseNumber(card);
                            break;
                        case Constants.Documents.RATION_CARD:
                            viewModel.getLocalCopy().setRationCardNumber(card);
                            break;
                    }
                    callback.done(type, updated);
                }
                else
                    callback.showSnackbar("Enter a valid Card Number");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

            photo.setImageBitmap(bmp);
            photo.setScaleX(1f);
            photo.setScaleY(1f);
            uploadText.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);

            viewModel.setImage(reducedImage(bmp),type);
        }
    }

    private Bitmap reducedImage(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        int currSize = stream.toByteArray().length;
        if(currSize > Constants.Documents.ONE_MEGABYTE) {
            int width = (int) ((bmp.getWidth() * Constants.Documents.ONE_MEGABYTE)/currSize);
            int height = (int) ((bmp.getHeight() * Constants.Documents.ONE_MEGABYTE)/currSize);
            bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
        }
        return bmp;
    }
}
