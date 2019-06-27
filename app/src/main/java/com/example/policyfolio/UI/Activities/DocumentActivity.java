package com.example.policyfolio.UI.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.policyfolio.UI.Fragments.Document.DocumentFragment;
import com.example.policyfolio.UI.Fragments.Document.SelectedDocumentFragment;
import com.example.policyfolio.Util.CallBackListeners.DocumentCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.DocumentViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.policyfolio.R;

public class DocumentActivity extends AppCompatActivity implements DocumentCallback {

    private DocumentViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private DocumentFragment documentFragment;
    private SelectedDocumentFragment selectedDocumentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Document Vault");
        getSupportActionBar().setIcon(R.drawable.document_icon);

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);
        viewModel.initiateRepo(this);
        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        setUpFragment();
    }

    private void setUpFragment() {
        if(documentFragment == null)
            documentFragment = new DocumentFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,documentFragment).commit();
    }

    @Override
    public void uploadDocument(int type) {
        if(selectedDocumentFragment != null){
            getSupportFragmentManager().beginTransaction().remove(selectedDocumentFragment).commit();
        }
        selectedDocumentFragment = new SelectedDocumentFragment(this,type,false);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,selectedDocumentFragment).commit();
    }

    @Override
    public void viewUploadedDocument(int type) {
        if(selectedDocumentFragment != null){
            getSupportFragmentManager().beginTransaction().remove(selectedDocumentFragment).commit();
        }
        selectedDocumentFragment = new SelectedDocumentFragment(this,type,true);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,selectedDocumentFragment).commit();
    }

    @Override
    public void getImage() {
        if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constants.PermissionAndRequests.READ_PERMISSION);
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED))
                return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.PermissionAndRequests.PICK_IMAGE_REQUEST);
    }

    @Override
    public void done(final int type, boolean uploaded) {
        fragmentHolder.setAlpha(0.4f);
        progressBar.setVisibility(View.VISIBLE);
        if(uploaded && viewModel.getImage(type) == null){
            viewModel.deleteImage(type).observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if(aBoolean){
                        switch (type){
                            case Constants.Documents.ADHAAR:
                                viewModel.getLocalCopy().setAdhaarLink(null);
                                break;
                            case Constants.Documents.PASSPORT:
                                viewModel.getLocalCopy().setPassportLink(null);
                                break;
                            case Constants.Documents.PAN:
                                viewModel.getLocalCopy().setPanLink(null);
                                break;
                            case Constants.Documents.VOTER_ID:
                                viewModel.getLocalCopy().setVoterIdLink(null);
                                break;
                            case Constants.Documents.DRIVING_LICENSE:
                                viewModel.getLocalCopy().setDrivingLicenseLink(null);
                                break;
                            case Constants.Documents.RATION_CARD:
                                viewModel.getLocalCopy().setRationCardLink(null);
                                break;
                        }
                        saveChanges();
                    }
                    else {
                        fragmentHolder.setAlpha(1f);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(DocumentActivity.this, "Unable to modify image database", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            viewModel.uploadImage(type).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if(s!=null){
                        switch (type){
                            case Constants.Documents.ADHAAR:
                                viewModel.getLocalCopy().setAdhaarLink(s);
                                break;
                            case Constants.Documents.PASSPORT:
                                viewModel.getLocalCopy().setPassportLink(s);
                                break;
                            case Constants.Documents.PAN:
                                viewModel.getLocalCopy().setPanLink(s);
                                break;
                            case Constants.Documents.VOTER_ID:
                                viewModel.getLocalCopy().setVoterIdLink(s);
                                break;
                            case Constants.Documents.DRIVING_LICENSE:
                                viewModel.getLocalCopy().setDrivingLicenseLink(s);
                                break;
                            case Constants.Documents.RATION_CARD:
                                viewModel.getLocalCopy().setRationCardLink(s);
                                break;
                        }
                        saveChanges();
                    }
                    else {
                        fragmentHolder.setAlpha(1f);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(DocumentActivity.this, "Unable to modify image database", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void saveChanges() {
        viewModel.updateChanges().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                fragmentHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if(aBoolean){
                    Toast.makeText(DocumentActivity.this,"Changes Successful",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(DocumentActivity.this,"Error while updating Database",Toast.LENGTH_LONG).show();
                }
                getSupportFragmentManager().beginTransaction().remove(selectedDocumentFragment).commit();
                selectedDocumentFragment = null;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(selectedDocumentFragment != null){
            getSupportFragmentManager().beginTransaction().remove(selectedDocumentFragment).commit();
            selectedDocumentFragment = null;
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(selectedDocumentFragment != null){
            selectedDocumentFragment.onActivityResult(requestCode,resultCode,data);
        }
    }
}
