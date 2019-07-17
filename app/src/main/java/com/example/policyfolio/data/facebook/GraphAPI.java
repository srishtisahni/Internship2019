package com.example.policyfolio.data.facebook;
import android.os.Bundle;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.data.facebook.dataclasses.FacebookData;
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

    //All **EXCEPTIONS** are printed in error log with the tag "EXCEPTION" along with the exception Message

    public static GraphAPI getInstance() {
        //Singleton Pattern
        if(INSTANCE == null)
            INSTANCE = new GraphAPI();
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    public LiveData<FacebookData> getFacebookProfile(AccessToken accessToken) {
        //Fetching information from facebook profile of the user
        final MutableLiveData<FacebookData> facebookFetch = new MutableLiveData<>();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                FacebookData facebookData = null;
                try {
                    long id = object.getLong(ID);
                    String name = object.getString(NAME);
                    String email = object.getString(EMAIL);
                    String birthday = object.getString(BIRTHDAY);
                    String gender = object.getString(GENDER);
                    JSONObject location = object.getJSONObject(LOCATION);
                    long locationId = location.getLong(ID);
                    String locationName = location.getString(NAME);
                    facebookData = new FacebookData(id,email,name,birthday,gender,locationId,locationName);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("EXCEPTION",e.getMessage());
                }
                facebookFetch.setValue(facebookData);
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", ID + "," + EMAIL + "," + LOCATION + "," + NAME + "," + BIRTHDAY + "," + GENDER);
        request.setParameters(parameters);
        request.executeAsync();
        return facebookFetch;
    }
}
