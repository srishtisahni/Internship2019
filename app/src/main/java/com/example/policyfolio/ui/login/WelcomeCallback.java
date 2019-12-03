package com.example.policyfolio.ui.login;

import com.example.policyfolio.data.firebase.classes.LogInData;

public interface WelcomeCallback {
    void openLoginSignUp();
    void openHome(LogInData logInData);
}
