package com.example.policyfolio.ViewModels.WithUser;

import androidx.lifecycle.LiveData;
import com.example.policyfolio.Data.Firebase.Classes.Query;
import com.example.policyfolio.ViewModels.Base.BaseViewModelWithUser;

public class HelpViewModel extends BaseViewModelWithUser {

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
        return getRepository().saveQuery(new Query(getUid(),query,type,false));
    }
}
