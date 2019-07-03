package com.example.policyfolio.UI.Base;

import androidx.recyclerview.widget.RecyclerView;

public interface BaseActivityFragmentCallback {
    void openListSheet(int type, RecyclerView.Adapter adapter);
    void closeListSheet();
}
