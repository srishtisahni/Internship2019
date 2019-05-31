package com.example.policyfolio.Repo.Facebook;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.util.Log;

import com.example.policyfolio.Data.Facebook;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GraphAPI {

    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String LOCATION = "location";
    private static final String NAME = "name";
    private static final String BIRTHDAY = "birthday";
    private static final String GENDER = "gender";

    public void getFacebookProfile(final MutableLiveData<Facebook> facebookFetch, AccessToken accessToken) {
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
    }
}
