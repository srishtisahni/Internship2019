package com.example.policyfolio.UI.Activities.NavigationActionActivities;

import android.os.Bundle;

import com.example.policyfolio.UI.Fragments.NavigationActionFragments.PromotionsFragment;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.NavigationViewModels.PromotionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.policyfolio.R;

public class PromotionsActivity extends AppCompatActivity {

    private PromotionViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private PromotionsFragment promotionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Promotions and Offers");
        getSupportActionBar().setIcon(R.drawable.promotion_icon);

        viewModel = ViewModelProviders.of(this).get(PromotionViewModel.class);
        viewModel.initiateRepo(this);
        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        setUpFragment();
    }

    private void setUpFragment() {
        if(promotionsFragment == null)
            promotionsFragment = new PromotionsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,promotionsFragment).commit();
    }

}
