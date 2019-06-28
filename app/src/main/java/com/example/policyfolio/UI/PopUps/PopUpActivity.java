package com.example.policyfolio.UI.PopUps;

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
import com.example.policyfolio.Data.Local.Classes.User;
import com.example.policyfolio.R;
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
                String id = getIntent().getExtras().getString(Constants.User.ID,null);
                if(id!=null){
                    Bundle bundle = getIntent().getExtras();
                    User user = new User();
                    user.setBirthday(bundle.getLong(Constants.User.BIRTHDAY));
                    user.setCity(bundle.getString(Constants.User.CITY));
                    user.setEmail(bundle.getString(Constants.User.EMAIL));
                    user.setGender(bundle.getInt(Constants.User.GENDER));
                    user.setId(bundle.getString(Constants.User.ID));
                    user.setName(bundle.getString(Constants.User.NAME));
                    user.setPhone(bundle.getString(Constants.User.PHONE));
                    user.setType(bundle.getInt(Constants.User.LOGIN_TYPE));

                    viewModel.updateUser(user);
                }
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,infoPopUp).commit();
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
                    setResult(Constants.PermissionAndRequests.UPDATE_RESULT);
                    finish();
                }
                else {
                    Toast.makeText(PopUpActivity.this, "Unable to update Information", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
