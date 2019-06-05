package com.example.policyfolio;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;

public class Constants {
    public static final String LOGIN_SHARED_PREFERENCE_KEY = "user_logged_in";
    public static final String DATABASE_NAME = "policy_folio_database";
    public static final int DATABASE_VERSION = 1;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
    public static final SimpleDateFormat FACEBOOK_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public static class User {
        public static final String EMAIL = "email";
        public static final String USER = "user";

        public static class Gender{
            public static final int GENDER_NOT_DISCLOSE = 0;
            public static final int GENDER_MALE = 1;
            public static final int GENDER_FEMALE = 2;
            public static final int GENDER_OTHER = 3;
        }
    }
    public static class SharedPreferenceKeys {

        public static final String LOGGED_IN = "log_in";
        public static final String TYPE = "type";
        public static final String FIREBASE_UID = "firebase_token";

        public static class Type{
            public static final int GOOGLE = 0;
            public static final int FACEBOOK = 1;
            public static final int PHONE = 3;
            public static final int EMAIL = 4;
        }
    }
    public static class Facebook{
        public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");

        public static final String EMAIL = "email";
        public static final String PROFILE = "public_profile";
        public static final String BIRTHDAY = "user_birthday";
        public static final String GENDER = "user_gender";
        public static final String LOCATION = "user_location";
        public static final String ID = "id";

        public static class Login{
            public static final int LOGGED_IN = 1;
            public static final int LOGIN_FAILED = 2;
            public static final int LOGIN_CANCELLED = 3;
            public static final int LOGIN_ERROR = 4;
        }
    }

    public static class Google{
        public static final int SIGN_IN_RC = 9001;
    }

    public static class FirebaseDataManagement {
        public static final String COLLECTION_USERS = "users";
        public static final int UPDATE_REQUEST = 1001;
        public static final int UPDATE_RESULT = 1002;
    }

    public static class PopUps{
        public static final String POPUP_TYPE = "type";

        public static class Type{
            public static final int EMAIL_POPUP = 1;
            public static final int INFO_POPUP = 2;
        }
    }
}
