package com.example.policyfolio.UI.Activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.policyfolio.Repo.Database.DataClasses.Notifications;
import com.example.policyfolio.UI.Fragments.Document.DocumentFragment;
import com.example.policyfolio.UI.Fragments.Document.SelectedDocumentFragment;
import com.example.policyfolio.Util.CallBackListeners.DocumentCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Util.Receivers.PremiumDuesReceiver;
import com.example.policyfolio.ViewModels.DocumentViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.policyfolio.R;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class DocumentActivity extends AppCompatActivity implements DocumentCallback, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView name;

    private DocumentViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private DocumentFragment documentFragment;
    private SelectedDocumentFragment selectedDocumentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        name = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.nav_name);
        name.setText(getIntent().getStringExtra(Constants.User.NAME));

        getSupportActionBar().setTitle("Document Vault");

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);
        viewModel.initiateRepo(this);

        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));
        viewModel.setLoginType(getIntent().getIntExtra(Constants.User.LOGIN_TYPE,-1));

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        setUpDrawer();
        setUpFragment();
    }

    private void setUpDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.document_icon, this.getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progressBar.getVisibility() == View.GONE) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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
                                viewModel.getLocalCopy().setAdhaarNumber(null);
                                break;
                            case Constants.Documents.PASSPORT:
                                viewModel.getLocalCopy().setPassportLink(null);
                                viewModel.getLocalCopy().setPassportNumber(null);
                                break;
                            case Constants.Documents.PAN:
                                viewModel.getLocalCopy().setPanLink(null);
                                viewModel.getLocalCopy().setPanNumber(null);
                                break;
                            case Constants.Documents.VOTER_ID:
                                viewModel.getLocalCopy().setVoterIdLink(null);
                                viewModel.getLocalCopy().setVoterIdNumber(null);
                                break;
                            case Constants.Documents.DRIVING_LICENSE:
                                viewModel.getLocalCopy().setDrivingLicenseLink(null);
                                viewModel.getLocalCopy().setDrivingLicenseNumber(null);
                                break;
                            case Constants.Documents.RATION_CARD:
                                viewModel.getLocalCopy().setRationCardLink(null);
                                viewModel.getLocalCopy().setRationCardNumber(null);
                                break;
                        }
                        Log.e("DOCUMENT VAULT","Deleted Image");
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
                        Log.e("DOCUMENT VAULT","Uploaded Image");
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
                if(selectedDocumentFragment !=null) {
                    getSupportFragmentManager().beginTransaction().remove(selectedDocumentFragment).commit();
                    selectedDocumentFragment = null;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(selectedDocumentFragment != null){
                getSupportFragmentManager().beginTransaction().remove(selectedDocumentFragment).commit();
                selectedDocumentFragment = null;
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(selectedDocumentFragment != null){
            selectedDocumentFragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.add_policy:
                addPolicy();
                break;
            case R.id.logout:
                logOut();
                break;
            case R.id.nominee_support:
                nomineeDashboard();
                break;
            case R.id.help:
                getHelp();
                break;
            case R.id.promotions:
                promotions();
                break;
            case R.id.claim_support:
                claimSupport();
                break;
            case R.id.documents:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addPolicy() {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        intent.putExtra(Constants.User.NAME,name.getText().toString());
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
        finish();
    }

    private void claimSupport() {
        Intent intent = new Intent(this, ClaimSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        intent.putExtra(Constants.User.NAME,name.getText().toString());
        startActivityForResult(intent,Constants.PermissionAndRequests.CLAIMS_REQUEST);
        finish();
    }

    private void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        intent.putExtra(Constants.User.NAME,name.getText().toString());
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
        finish();
    }

    private void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        intent.putExtra(Constants.User.NAME,name.getText().toString());
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
        finish();
    }

    private void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        intent.putExtra(Constants.User.NAME,name.getText().toString());
        startActivityForResult(intent,Constants.PermissionAndRequests.NOMINEE_DASHBOARD_REQUEST);
        finish();
    }

    private void logOut() {
        fragmentHolder.setAlpha(.4f);
        progressBar.setVisibility(View.VISIBLE);
        cancelNotifications();
        viewModel.logOut().observe(DocumentActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                fragmentHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if(aBoolean){
                    getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(DocumentActivity.this,LoginSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(DocumentActivity.this,"LogOut Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cancelNotifications() {
        getSharedPreferences(Constants.Notification.NOTIFICATION_SHARED_PREFERENCE,MODE_PRIVATE).edit().clear().apply();
        viewModel.getAllNotificatios().observe(this, new Observer<List<Notifications>>() {
            @Override
            public void onChanged(List<Notifications> notifications) {
                if(!notifications.isEmpty()) {
                    for (int i = 0; i < notifications.size(); i++) {
                        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent intent = new Intent(DocumentActivity.this, PremiumDuesReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(DocumentActivity.this, (int) notifications.get(i).getId(), intent, PendingIntent.FLAG_NO_CREATE);
                        if (pendingIntent != null) {
                            alarm.cancel(pendingIntent);
                        }
                    }
                    viewModel.deleteAllNotifications();
                    Log.e("NOTIFICATIONS", "Cancelled");
                }
            }
        });
    }
}
