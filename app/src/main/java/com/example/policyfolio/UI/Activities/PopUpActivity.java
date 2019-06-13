package com.example.policyfolio.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.PopUpCallBack;
import com.example.policyfolio.UI.PopUps.EmailPopUp;
import com.example.policyfolio.UI.PopUps.InfoPopUp;
import com.example.policyfolio.ViewModels.PopUpViewModel;

public class PopUpActivity extends AppCompatActivity implements PopUpCallBack {

    private PopUpViewModel viewModel;

    private CardView popUpHolder;
    private ProgressBar progressBar;

    private EmailPopUp emailPopUp;
    private InfoPopUp infoPopUp;

    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        viewModel = ViewModelProviders.of(this).get(PopUpViewModel.class);
        viewModel.initiateRepo(this);

        popUpHolder = findViewById(R.id.card_view);
        progressBar = findViewById(R.id.progress_bar);

        type = getIntent().getExtras().getInt(Constants.PopUps.POPUP_TYPE,-1);
        createPopUps();
    }

    private void createPopUps() {
        switch (type){
            case Constants.PopUps.Type.EMAIL_POPUP:
                emailPopUp = new EmailPopUp(this);
                String email = getIntent().getExtras().getString(Constants.User.EMAIL,null);
                if(email!=null) {
                    Bundle args = new Bundle();
                    args.putString(Constants.User.EMAIL, email);
                    emailPopUp.setArguments(args);
                }
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,emailPopUp).commit();
                break;
            case Constants.PopUps.Type.INFO_POPUP:
                infoPopUp = new InfoPopUp(this);
                String id = getIntent().getExtras().getString(Constants.LoginInInfo.FIREBASE_UID,null);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,infoPopUp).commit();
                if(id!=null){
                    progressBar.setVisibility(View.VISIBLE);
                    popUpHolder.setAlpha(0.5f);
                    viewModel.fetchUser(id).observe(this, new Observer<User>() {
                        @Override
                        public void onChanged(@Nullable User user) {
                            popUpHolder.setAlpha(1f);
                            progressBar.setVisibility(View.GONE);

                            if(user!=null) {
                                viewModel.updateUser(user);
                                infoPopUp.setAvailableInfo();
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void ForgotPassword() {
        popUpHolder.setAlpha(0.5f);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.resetPassword().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                popUpHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if(aBoolean) {
                    Toast.makeText(PopUpActivity.this, "A Password Reset email has been sent to your email Id", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(PopUpActivity.this, "The account doesn't exist", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void updateInfo() {
        popUpHolder.setAlpha(0.5f);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.updateFirebaseUser().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                popUpHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if(aBoolean) {
                    Toast.makeText(PopUpActivity.this, "Information Updated", Toast.LENGTH_LONG).show();
                    Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.User.USER,viewModel.getUser());
                    data.putExtras(bundle);
                    setResult(Constants.PermissionAndRequests.UPDATE_RESULT,data);
                    finish();
                }
                else {
                    Toast.makeText(PopUpActivity.this, "System Error Occurred", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
