package com.example.policyfolio.UI.Activities.NavigationActionActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.UI.Fragments.NavigationActionFragments.AddNomineeFragment;
import com.example.policyfolio.UI.Fragments.NavigationActionFragments.NomineeDashboardFragment;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.NomineeCallback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.NavigationViewModels.NomineeViewModel;

public class NomineeSupportActivity extends AppCompatActivity implements NomineeCallback {

    private NomineeViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private NomineeDashboardFragment nomineeDashboardFragment;
    private AddNomineeFragment addNomineeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nominee_support);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.nominee_icon));
        viewModel = ViewModelProviders.of(this).get(NomineeViewModel.class);
        viewModel.initiateRepo(this);

        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        addDashboard();
    }

    private void addDashboard() {
        getSupportActionBar().setTitle("Nominee Dashboard");

        fragmentHolder.setAlpha(1f);
        progressBar.setVisibility(View.GONE);

        if(nomineeDashboardFragment == null){
            nomineeDashboardFragment = new NomineeDashboardFragment(this);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,nomineeDashboardFragment).commit();
    }

    @Override
    public void addNominee() {
        getSupportActionBar().setTitle("Add Nominee");
        if(addNomineeFragment == null){
            addNomineeFragment = new AddNomineeFragment(this);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,addNomineeFragment).commit();
    }

    @Override
    public void done() {
        fragmentHolder.setAlpha(0.4f);
        progressBar.setVisibility(View.VISIBLE);

        viewModel.addNominee().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                fragmentHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if (aBoolean) {
                    getSupportFragmentManager().beginTransaction().remove(addNomineeFragment).commit();
                    addNomineeFragment = null;
                    getSupportActionBar().setTitle("Nominee Dashboard");
                } else {
                    Toast.makeText(NomineeSupportActivity.this, "Unable to add Nominee.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(addNomineeFragment!=null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Do you want to exit without saving the nominee")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getSupportActionBar().setTitle("Nominee Dashboard");
                            getSupportFragmentManager().beginTransaction().remove(addNomineeFragment).commit();
                            addNomineeFragment = null;
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
        else {
            super.onBackPressed();
        }
    }
}
