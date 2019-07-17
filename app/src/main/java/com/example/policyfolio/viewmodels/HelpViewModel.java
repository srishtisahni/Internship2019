package com.example.policyfolio.viewmodels;

import androidx.lifecycle.LiveData;
import com.example.policyfolio.data.firebase.classes.Query;
import com.example.policyfolio.viewmodels.base.BaseViewModelNavigation;

public class HelpViewModel extends BaseViewModelNavigation {

    private int type;
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LiveData<String> saveQuery() {
        return getRepository().saveQuery(new Query(getUid(),query,type,false,System.currentTimeMillis()/1000));
    }
}
