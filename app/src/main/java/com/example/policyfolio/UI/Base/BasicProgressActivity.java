package com.example.policyfolio.UI.Base;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.policyfolio.R;
import com.google.android.material.snackbar.Snackbar;

public class BasicProgressActivity extends AppCompatActivity {

    private FrameLayout snackbar;
    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);
        snackbar = findViewById(R.id.snackbar_action);
    }


    protected void startProgress(){
        fragmentHolder.setAlpha(0.4f);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void endProgress() {
        fragmentHolder.setAlpha(1f);
        progressBar.setVisibility(View.GONE);
    }

    protected boolean inProgress(){
        return progressBar.getVisibility() == View.VISIBLE;
    }

    public void showSnackbar(String text) {
        Snackbar.make(snackbar,text,Snackbar.LENGTH_LONG).show();
    }
}
