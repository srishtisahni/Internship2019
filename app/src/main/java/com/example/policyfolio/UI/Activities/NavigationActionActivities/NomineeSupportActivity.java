package com.example.policyfolio.UI.Activities.NavigationActionActivities;

import android.os.Bundle;

import com.example.policyfolio.UI.Fragments.NomineeDashboardFragment;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.NomineeCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.policyfolio.R;

public class NomineeSupportActivity extends AppCompatActivity implements NomineeCallback {

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private NomineeDashboardFragment nomineeDashboardFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nominee_support);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.nominee_icon));

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
        //TODO Add Nominee Fragment
        Toast.makeText(this,"Add Nominee",Toast.LENGTH_LONG).show();
    }
}
