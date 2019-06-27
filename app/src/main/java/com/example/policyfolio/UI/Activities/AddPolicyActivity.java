package com.example.policyfolio.UI.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.AddPolicyCallback;
import com.example.policyfolio.UI.Fragments.AddPolicy.AddPolicyDetailsFragment;
import com.example.policyfolio.UI.Fragments.AddPolicy.BasicAddPolicyFragment;
import com.example.policyfolio.ViewModels.AddPolicyViewModel;

public class AddPolicyActivity extends AppCompatActivity implements AddPolicyCallback {


    private AddPolicyViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;
    private BasicAddPolicyFragment basicAddPolicyFragment;
    private AddPolicyDetailsFragment addPolicyDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_policy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Policy");
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.add_icon));

        viewModel = ViewModelProviders.of(this).get(AddPolicyViewModel.class);
        viewModel.initiateRepo(this);

        viewModel.setUid(getIntent().getStringExtra(Constants.User.ID));

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        addEntryFragment();
    }

    private void addEntryFragment() {
        fragmentHolder.setAlpha(1f);
        progressBar.setVisibility(View.GONE);
        basicAddPolicyFragment = new BasicAddPolicyFragment(this);
        basicAddPolicyFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,basicAddPolicyFragment).commit();
    }

    @Override
    public void next() {
        addPolicyDetailsFragment = new AddPolicyDetailsFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,addPolicyDetailsFragment).commit();
    }

    @Override
    public void done() {
        fragmentHolder.setAlpha(.4f);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.saveImage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s!=null){
                    viewModel.setPhotoUrl(s);
                    Toast.makeText(AddPolicyActivity.this,"Image Uploaded",Toast.LENGTH_LONG).show();
                }
                viewModel.savePolicy().observe(AddPolicyActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        fragmentHolder.setAlpha(1f);
                        progressBar.setVisibility(View.GONE);
                        if(!aBoolean)
                            Toast.makeText(AddPolicyActivity.this,"Unable to update Information",Toast.LENGTH_LONG).show();
                        else
                            setResult(Constants.PermissionAndRequests.ADD_POLICY_RESULT);
                    }
                });
            }
        });
        finish();
    }

    @Override
    public void addPolicyImage() {
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
    public void clickPolicyImage() {
        if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constants.PermissionAndRequests.READ_PERMISSION);
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED))
                return;
        }
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.PermissionAndRequests.CAPTURE_IMAGE_REQUEST);
    }

    @Override
    public void onBackPressed() {
        if(addPolicyDetailsFragment == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Do you want to exit without saving the Policy?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddPolicyActivity.super.onBackPressed();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
        else {
            getSupportFragmentManager().beginTransaction().remove(addPolicyDetailsFragment).commit();
            addPolicyDetailsFragment = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addPolicyDetailsFragment.onActivityResult(requestCode,resultCode,data);
    }
}
