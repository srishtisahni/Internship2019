package com.example.policyfolio.UI.Fragments.Document;


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
import android.widget.Toast;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.DocumentCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.DocumentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private boolean uploaded;

    public SelectedDocumentFragment() {
        // Required empty public constructor
    }

    public SelectedDocumentFragment(DocumentCallback callback, int type, boolean uploaded) {
        this.callback = callback;
        this.type = type;
        this.uploaded = uploaded;
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
        String text = "Upload File Here.";
        switch (type){
            case Constants.Documents.ADHAAR:
                cardText.setText(getResources().getString(R.string.adhaar_card));
                text = "Upload " + getResources().getString(R.string.adhaar_card) + " Here.";
                break;
            case Constants.Documents.PASSPORT:
                cardText.setText(getResources().getString(R.string.passport));
                text = "Upload " + getResources().getString(R.string.passport) + " Here.";
                break;
            case Constants.Documents.PAN:
                cardText.setText(getResources().getString(R.string.pan_card));
                text = "Upload " + getResources().getString(R.string.pan_card) + " Here.";
                break;
            case Constants.Documents.VOTER_ID:
                cardText.setText(getResources().getString(R.string.voter_id));
                text = "Upload " + getResources().getString(R.string.voter_id) + " Here.";
                break;
            case Constants.Documents.DRIVING_LICENSE:
                cardText.setText(getResources().getString(R.string.driving_license));
                text = "Upload " + getResources().getString(R.string.driving_license) + " Here.";
                break;
            case Constants.Documents.RATION_CARD:
                cardText.setText(getResources().getString(R.string.ration_card));
                text = "Upload " + getResources().getString(R.string.ration_card) + " Here.";
                break;
        }
        uploadText.setText(text);

        if(!uploaded){
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            done.setVisibility(View.GONE);
        }
        else {
            uploadText.setVisibility(View.GONE);
        }
    }

    private void setUpListeners() {
        if(!uploaded) {
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.getImage();
                }
            });
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.setImageDrawable(getResources().getDrawable(R.drawable.upload));
                photo.setScaleX(0.5f);
                photo.setScaleY(0.5f);
                uploadText.setVisibility(View.VISIBLE);
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
                if(cardNumber.getText().toString().length()>0) {
                    callback.done();
                }
                else
                    Toast.makeText(getContext(),"Enter a valid Card Number",Toast.LENGTH_LONG).show();
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

            //TODO upload Image
        }
    }
}
