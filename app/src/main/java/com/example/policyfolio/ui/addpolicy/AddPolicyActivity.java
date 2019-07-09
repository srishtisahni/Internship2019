package com.example.policyfolio.ui.addpolicy;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.data.local.classes.Notifications;
import com.example.policyfolio.ui.bottomsheets.ListBottomSheet;
import com.example.policyfolio.ui.claim.ClaimSupportActivity;
import com.example.policyfolio.ui.document.DocumentActivity;
import com.example.policyfolio.ui.help.HelpActivity;
import com.example.policyfolio.ui.login.LoginSignUpActivity;
import com.example.policyfolio.ui.Nominee.NomineeSupportActivity;
import com.example.policyfolio.ui.promotions.PromotionsActivity;
import com.example.policyfolio.ui.base.BaseNavigationActivity;
import com.example.policyfolio.ui.base.ParentChildNavigationCallback;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.util.receivers.PremiumDuesReceiver;
import com.example.policyfolio.viewmodels.AddPolicyViewModel;

import java.util.List;

public class AddPolicyActivity extends BaseNavigationActivity implements AddPolicyCallback, ParentChildNavigationCallback {

    private AddPolicyViewModel viewModel;

    private BasicAddPolicyFragment basicAddPolicyFragment;
    private AddPolicyDetailsFragment addPolicyDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        super.setCallback(this);
        super.setMenuSelection(R.id.add_policy);
        getSupportActionBar().setTitle("Add Policy");

        viewModel = ViewModelProviders.of(this).get(AddPolicyViewModel.class);
        viewModel.initiateRepo(this);

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
                        Toast.makeText(AddPolicyActivity.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                        Log.e("IMAGE", "Uploaded");
                    }
                    viewModel.savePolicy().observe(AddPolicyActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            AddPolicyActivity.super.endProgress();
                            Log.e("POLICY", "Created");
                            if (!aBoolean)
                                showSnackbar("Unable to update Information");
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
                        showSnackbar("Unable to update Information");
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
        startActivityForResult(intent,Constants.PermissionAndRequests.DOCUMENTS_REQUEST);
        finish();
    }

    @Override
    public void claimSupport() {
        Intent intent = new Intent(this, ClaimSupportActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.CLAIMS_REQUEST);
        finish();
    }

    @Override
    public void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
        finish();
    }

    @Override
    public void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
        finish();
    }

    @Override
    public void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
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

                    Intent intent = new Intent(AddPolicyActivity.this, LoginSignUpActivity.class);
                    intent.putExtra(Constants.LoginInInfo.POST_LOGOUT,true);
                    startActivity(intent);
                    finish();
                }
                else {
                    showSnackbar("Unable to update Information");
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
        if(isSheetOpen()){
            super.collapseSheet();
        }
        else if (super.isDrawerOpen()) {
            super.closeDrawer();
        }
        else {
            if(addPolicyDetailsFragment == null) {
                super.onBackPressed();
            }
            else {
                getSupportFragmentManager().beginTransaction().remove(addPolicyDetailsFragment).commit();
                addPolicyDetailsFragment = null;
            }
        }
    }

    @Override
    public void openListSheet(int type, RecyclerView.Adapter adapter) {
        ListBottomSheet listBottomSheet = new ListBottomSheet(type,adapter);
        super.expandSheet(listBottomSheet);
    }

    @Override
    public void closeListSheet() {
        super.collapseSheet();
    }
}
