package com.example.policyfolio.UI.Activities.NavigationActionActivities;

import android.os.Bundle;

import com.example.policyfolio.UI.Fragments.NavigationActionFragments.NeedHelpFragment;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.NeedHelpCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.NavigationViewModels.HelpViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.policyfolio.R;

public class HelpActivity extends AppCompatActivity implements NeedHelpCallback {

    private HelpViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private NeedHelpFragment needHelpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Need Help");
        getSupportActionBar().setIcon(R.drawable.help_icon);

        viewModel = ViewModelProviders.of(this).get(HelpViewModel.class);
        viewModel.initiateRepo(this);
        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));

        progressBar = findViewById(R.id.progress_bar);
        fragmentHolder = findViewById(R.id.fragment_holder);
        
        setUpFragment();
    }

    private void setUpFragment() {
        if(needHelpFragment == null)
            needHelpFragment = new NeedHelpFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,needHelpFragment).commit();
    }

    @Override
    public void save() {
        viewModel.saveQuery().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s == null){
                    Toast.makeText(HelpActivity.this,"Unable to update Information",Toast.LENGTH_LONG).show();
                }
                else {
                    setResult(Constants.PermissionAndRequests.HELP_RESULT);
                    finish();
                }
            }
        });
    }
}
