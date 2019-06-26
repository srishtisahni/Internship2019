package com.example.policyfolio.UI.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.policyfolio.UI.Fragments.Claim.OnGoingFragment;
import com.example.policyfolio.UI.Fragments.Claim.ResolvedFragment;
import com.example.policyfolio.UI.Fragments.Claim.ClaimDashboardFragment;
import com.example.policyfolio.UI.Fragments.Claim.TrackClaimFragment;
import com.example.policyfolio.Util.CallBackListeners.NavigationCallbacks.ClaimCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.ClaimViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.policyfolio.R;

import java.util.List;

public class ClaimSupportActivity extends AppCompatActivity implements ClaimCallback {

    private ClaimViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

    private ClaimDashboardFragment claimDashboardFragment;
    private TrackClaimFragment trackClaimFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_support);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Claims");
        getSupportActionBar().setIcon(R.drawable.claim_activity_icon);

        viewModel = ViewModelProviders.of(this).get(ClaimViewModel.class);
        viewModel.initiateRepo(this);
        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        setUpDashboard();
    }

    private void setUpDashboard() {
        if(claimDashboardFragment == null)
            claimDashboardFragment = new ClaimDashboardFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,claimDashboardFragment).commit();
    }

    @Override
    public void claimAssistance() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        intent.putExtra(Constants.Query.TYPE,Constants.Query.Type.CLAIMS);
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
    }

    @Override
    public void legalSupport() {
        Toast.makeText(this,"Under Construction",Toast.LENGTH_LONG).show();
    }

    @Override
    public void trackClaim() {
        trackClaimFragment = new TrackClaimFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,trackClaimFragment).commit();
    }

    @Override
    public void callAssistance() {
        Uri u = Uri.parse("tel:" + Constants.Claims.NUMBER);
        Intent intent = new Intent(Intent.ACTION_DIAL, u);
        try
        {
            startActivity(intent);
        }
        catch (SecurityException s)
        {
            Toast.makeText(this, "Unable to Process Call", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.PermissionAndRequests.HELP_REQUEST && resultCode == Constants.PermissionAndRequests.HELP_RESULT)
            Toast.makeText(this,"We will get back on your query as soon as possible",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if(trackClaimFragment!=null){
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for(int i=0;i<fragmentList.size();i++){
                if(fragmentList.get(i) instanceof  TrackClaimFragment ||  fragmentList.get(i) instanceof OnGoingFragment || fragmentList.get(i) instanceof ResolvedFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragmentList.get(i)).commit();
                    trackClaimFragment = null;
                }
            }
        }
        else
            super.onBackPressed();
    }
}
