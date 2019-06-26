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
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

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
    public void done() {

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
