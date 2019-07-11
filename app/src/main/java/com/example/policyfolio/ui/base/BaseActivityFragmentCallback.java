package com.example.policyfolio.ui.base;

import androidx.recyclerview.widget.RecyclerView;

public interface BaseActivityFragmentCallback {
    void openListSheet(int type, RecyclerView.Adapter adapter);
    void closeListSheet();
    void setBackgroundToGreen();
    void setBackgroundToWhite();
}
