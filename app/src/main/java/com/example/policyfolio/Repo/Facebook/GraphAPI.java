package com.example.policyfolio.Repo.Facebook;
import android.os.Bundle;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.DataClasses.Facebook;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class GraphAPI {

    public static GraphAPI INSTANCE;
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String LOCATION = "location";
    private static final String NAME = "name";
    private static final String BIRTHDAY = "birthday";
    private static final String GENDER = "gender";

    private GraphAPI(){

    }

    public static GraphAPI getInstance() {
        if(INSTANCE == null)
            INSTANCE = new GraphAPI();
        return INSTANCE;
    }

    public LiveData<Facebook> getFacebookProfile(AccessToken accessToken) {
        final MutableLiveData<Facebook> facebookFetch = new MutableLiveData<>();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Facebook facebook = null;
                try {
                    long id = object.getLong(ID);
                    String name = object.getString(NAME);
                    String email = object.getString(EMAIL);
                    String birthday = object.getString(BIRTHDAY);
                    String gender = object.getString(GENDER);
                    JSONObject location = object.getJSONObject(LOCATION);
                    long locationId = location.getLong(ID);
                    String locationName = location.getString(NAME);
                    facebook = new Facebook(id,email,name,birthday,gender,locationId,locationName);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                facebookFetch.setValue(facebook);
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", ID + "," + EMAIL + "," + LOCATION + "," + NAME + "," + BIRTHDAY + "," + GENDER);
        request.setParameters(parameters);
        request.executeAsync();
        return facebookFetch;
    }
}
