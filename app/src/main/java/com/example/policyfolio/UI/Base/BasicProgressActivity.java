package com.example.policyfolio.UI.Base;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.example.policyfolio.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

public class BasicProgressActivity extends AppCompatActivity {

    private FrameLayout snackbar;
    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private FrameLayout sheetFragmentHolder;
    private ProgressBar sheetProgressBar;
    private CoordinatorLayout sheet;
    private BottomSheetBehavior sheetBehavior;
    private Fragment sheetFragment;
    private View coverFragment;

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

    protected void setUpBottomSheet() {
        sheet = findViewById(R.id.bottom_sheet);
        sheetFragmentHolder = findViewById(R.id.sheet_fragment_holder);
        sheetProgressBar = findViewById(R.id.sheet_progress_bar);
        sheetBehavior = BottomSheetBehavior.from(sheet);
        coverFragment = findViewById(R.id.cover_fragment);

        sheet.setVisibility(View.VISIBLE);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    protected void expandSheet(Fragment fragment){
        this.sheetFragment = fragment;
        getSupportFragmentManager().beginTransaction().add(R.id.sheet_fragment_holder,fragment).commit();
        coverFragment.setVisibility(View.VISIBLE);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    protected void collapseSheet(){
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        coverFragment.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().remove(sheetFragment).commit();
    }

    protected void startSheetProgress() {
        sheetFragmentHolder.setAlpha(0.4f);
        sheetProgressBar.setVisibility(View.VISIBLE);
    }

    protected void endSheetProgress() {
        sheetFragmentHolder.setAlpha(1f);
        sheetProgressBar.setVisibility(View.GONE);
    }

    protected boolean isSheetOpen() {
        return sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }
}
