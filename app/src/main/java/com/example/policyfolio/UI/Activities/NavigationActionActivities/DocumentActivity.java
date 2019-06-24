package com.example.policyfolio.UI.Activities.NavigationActionActivities;

import android.os.Bundle;

import com.example.policyfolio.UI.Fragments.NavigationActionFragments.DocumentFragment;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.DocumentCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.NavigationViewModels.DocumentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.policyfolio.R;

public class DocumentActivity extends AppCompatActivity implements DocumentCallback {

    private DocumentViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private DocumentFragment documentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Document Vault");
        getSupportActionBar().setIcon(R.drawable.document_icon);

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);
        viewModel.initiateRepo(this);
        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        setUpFragment();
    }

    private void setUpFragment() {
        if(documentFragment == null)
            documentFragment = new DocumentFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,documentFragment).commit();
    }

}
