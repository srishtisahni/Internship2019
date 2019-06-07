package com.example.policyfolio.UI.Activities.NavigationActionActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.policyfolio.R;
import com.example.policyfolio.UI.Fragments.NavigationActionFragments.BasicAddPolicyFragment;

public class AddPolicyActivity extends AppCompatActivity {

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;
    private BasicAddPolicyFragment basicAddPolicyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_policy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Policy");
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.add_icon));

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        addEntryFragment();
    }

    private void addEntryFragment() {
        fragmentHolder.setAlpha(1f);
        progressBar.setVisibility(View.GONE);
        basicAddPolicyFragment = new BasicAddPolicyFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,basicAddPolicyFragment).commit();
    }

}
