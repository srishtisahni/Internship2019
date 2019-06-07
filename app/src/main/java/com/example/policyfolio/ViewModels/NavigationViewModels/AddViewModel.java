package com.example.policyfolio.ViewModels.NavigationViewModels;

import androidx.lifecycle.ViewModel;

public class AddViewModel extends ViewModel {
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
