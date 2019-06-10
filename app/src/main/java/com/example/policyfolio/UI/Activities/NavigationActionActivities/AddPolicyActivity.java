package com.example.policyfolio.UI.Activities.NavigationActionActivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.AddPolicyCallback;
import com.example.policyfolio.UI.Fragments.NavigationActionFragments.AddPolicyDetailsFragment;
import com.example.policyfolio.UI.Fragments.NavigationActionFragments.BasicAddPolicyFragment;
import com.example.policyfolio.ViewModels.NavigationViewModels.AddViewModel;

public class AddPolicyActivity extends AppCompatActivity implements AddPolicyCallback {


    private AddViewModel viewModel;

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

        viewModel = ViewModelProviders.of(this).get(AddViewModel.class);

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        addEntryFragment();
    }

    private void addEntryFragment() {
        fragmentHolder.setAlpha(1f);
        progressBar.setVisibility(View.GONE);
        basicAddPolicyFragment = new BasicAddPolicyFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,basicAddPolicyFragment).commit();
    }

    @Override
    public void next() {
//        addPolicyDetailsFragment = new AddPolicyDetailsFragment(this);
//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,addPolicyDetailsFragment).commit();
    }

    @Override
    public void done() {

    }

    @Override
    public void onBackPressed() {
        if(addPolicyDetailsFragment == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Do you want to exit without saving the policy")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
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
}
