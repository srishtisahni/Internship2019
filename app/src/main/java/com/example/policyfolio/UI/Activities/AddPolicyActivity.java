package com.example.policyfolio.UI.Activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.Repo.Database.DataClasses.Notifications;
import com.example.policyfolio.Util.CallBackListeners.ParentChildNavigationCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.AddPolicyCallback;
import com.example.policyfolio.UI.Fragments.AddPolicy.AddPolicyDetailsFragment;
import com.example.policyfolio.UI.Fragments.AddPolicy.BasicAddPolicyFragment;
import com.example.policyfolio.Util.Receivers.PremiumDuesReceiver;
import com.example.policyfolio.ViewModels.AddPolicyViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class AddPolicyActivity extends ParentNavigationActivity implements AddPolicyCallback, ParentChildNavigationCallback {

    private AddPolicyViewModel viewModel;

    private BasicAddPolicyFragment basicAddPolicyFragment;
    private AddPolicyDetailsFragment addPolicyDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        getSupportActionBar().setTitle("Add Policy");

        viewModel = ViewModelProviders.of(this).get(AddPolicyViewModel.class);
        viewModel.initiateRepo(this);

        viewModel.setUid(getIntent().getStringExtra(Constants.User.ID));
        viewModel.setLoginType(getIntent().getIntExtra(Constants.User.LOGIN_TYPE,-1));
        super.setCallback(this);

        addEntryFragment();
    }

    private void addEntryFragment() {
        super.endProgress();
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
        super.startProgress();
        if(viewModel.getBitmap() != null) {
            viewModel.saveImage().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    Log.e("IMAGE", "Uploading");
                    if (s != null) {
                        viewModel.setPhotoUrl(s);
                        Toast.makeText(AddPolicyActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                        Log.e("IMAGE", "Uploaded");
                    }
                    viewModel.savePolicy().observe(AddPolicyActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            AddPolicyActivity.super.endProgress();
                            Log.e("POLICY", "Created");
                            if (!aBoolean)
                                Toast.makeText(AddPolicyActivity.this, "Unable to update Information", Toast.LENGTH_LONG).show();
                            else
                                setResult(Constants.PermissionAndRequests.ADD_POLICY_RESULT);
                            finish();
                        }
                    });
                }
            });
        }
        else {
            viewModel.savePolicy().observe(AddPolicyActivity.this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    AddPolicyActivity.super.endProgress();
                    Log.e("POLICY", "Created");
                    if (!aBoolean)
                        Toast.makeText(AddPolicyActivity.this, "Unable to update Information", Toast.LENGTH_LONG).show();
                    else
                        setResult(Constants.PermissionAndRequests.ADD_POLICY_RESULT);
                    finish();
                }
            });
        }
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
    public void documentVault() {
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.DOCUMENTS_REQUEST);
        finish();
    }

    @Override
    public void claimSupport() {
        Intent intent = new Intent(this, ClaimSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.CLAIMS_REQUEST);
        finish();
    }

    @Override
    public void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
        finish();
    }

    @Override
    public void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
        finish();
    }

    @Override
    public void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.NOMINEE_DASHBOARD_REQUEST);
        finish();
    }

    @Override
    public void addPolicy() {

    }

    @Override
    public void logOut() {
        super.startProgress();
        cancelNotifications();
        viewModel.logOut().observe(AddPolicyActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                AddPolicyActivity.super.endProgress();
                if(aBoolean){
                    getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(AddPolicyActivity.this,LoginSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(AddPolicyActivity.this,"LogOut Failed",Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(AddPolicyActivity.this, PremiumDuesReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddPolicyActivity.this, (int) notifications.get(i).getId(), intent, PendingIntent.FLAG_NO_CREATE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addPolicyDetailsFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        if (super.isDrawerOpen()) {
            super.closeDrawer();
        } else {
            if(addPolicyDetailsFragment == null) {
                super.onBackPressed();
            }
            else {
                getSupportFragmentManager().beginTransaction().remove(addPolicyDetailsFragment).commit();
                addPolicyDetailsFragment = null;
            }
        }
    }
}
