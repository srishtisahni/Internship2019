package com.example.policyfolio.Util;

import android.content.Intent;
import android.hardware.camera2.CaptureRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Formatter;

public class Constants {
    public static final String LOGIN_SHARED_PREFERENCE_KEY = "user_logged_in";
    public static final String DATABASE_NAME = "policy_folio_database";
    public static final int DATABASE_VERSION = 1;

    public static class Time{
        public static final long EPOCH_DAY = 86400;
        public static final long EPOCH_WEEK = 604800;
        public static final long EPOCH_MONTH = 2629743;
        public static final long EPOCH_YEAR = 31556926;

        public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
    }
    public static class User {
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String ID = "id";
        public static final String BIRTHDAY = "birthday";
        public static final String CITY = "city";
        public static final String GENDER = "gender";
        public static final String NAME = "name";
        public static final String LOGIN_TYPE = "login_type";
        public static final String COMPLETED = "completed";

        public static class Gender{
            public static final int GENDER_NOT_DISCLOSE = 0;
            public static final int GENDER_MALE = 1;
            public static final int GENDER_FEMALE = 2;
            public static final int GENDER_OTHER = 3;
        }
    }
    public static class Policy{
        public static final String UPDATED_SHARED_PREFRENCE = "updated_policy";
        public static final int DISPLAY_PREMIUM = 0;
        public static final int DISPLAY_SUM = 1;
        public static final String NOMINEE = "nominee";

        public static class Type{
            public static final int LIFE_INSURANCE = 0;
            public static final int AUTO_INSURANCE = 1;
            public static final int HEALTH_INSURANCE = 2;
            public static final int CARD_INSURANCE = 3;
            public static final int TRAVEL_INSURANCE = 4;
            public static final int PROPERTY_INSURANCE = 5;
            public static final int BUSINESS_INSURANCE = 6;
            public static final int EMPLOYEE_INSURANCE = 7;
            public static final int APPLIANCE_INSURANCE = 8;
        }
        public static class Premium{
            public static final int PREMIUM_MONTHLY = 0;
            public static final int PREMIUM_QUATERLY = 1;
            public static final int PREMIUM_BI_ANNUALLY = 2;
            public static final int PREMIUM_ANNUALLY = 3;
        }
    }
    public static class LoginInInfo {
        public static final String LOGGED_IN = "log_in";
        public static final String TYPE = "type";
        public static final String FIREBASE_UID = "firebase_token";
        public static final String LOG_IN_DATA = "data";

        public static class Type{
            public static final int GOOGLE = 0;
            public static final int FACEBOOK = 1;
            public static final int PHONE = 2;
            public static final int EMAIL = 3;
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

    public static class FirebaseDataManager {
        public static final String COLLECTION_USERS = "users";
        public static final String COLLECTION_POLICIES = "policies";
        public static final String COLLECTION_PROVIDERS = "insurance_providers";
        public static final String COLLECTION_PRODUCTS = "insurance_products";
        public static final String COLLECTION_NOMINEE = "nominees";
        public static final String COLLECTION_QUERIES = "queries";
        public static final String COLLECTION_DOCUMENTS = "documents";

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

    public static class ListTypes {
        public static final int INSURANCE_TYPE = 0;
        public static final int INSURANCE_PROVIDER = 1;
        public static final int PREMIUM_FREQUENCY = 2;
        public static final int NOMINEE = 3;
        public static final int RELATIONSHIPS = 4;
        public static final int GENDER = 5;
        public static final int QUERY = 6;
    }

    public static class InsuranceProviders {
        public static final String TYPE = "type";
    }

    public static class Nominee {
        public static final String DEFAULT_PFID = "null";
        public static final String EMAIL = "email";
        public static final String PFID = "pfId";

        public static class Relation{
            public static final int MOTHER = 0;
            public static final int FATHER = 1;
            public static final int BROTHER = 2;
            public static final int SISTER = 3;
            public static final int HUSBAND = 4;
            public static final int WIFE = 5;
            public static final int SON = 6;
            public static final int DAUGHTER = 7;
        }
    }

    public static class PermissionAndRequests {
        public static final int READ_PERMISSION = 101;
        public static final int PICK_IMAGE_REQUEST = 102;
        public static final int CAPTURE_IMAGE_REQUEST = 103;
        public static final int UPDATE_REQUEST = 104;
        public static final int UPDATE_RESULT = 105;
        public static final int ADD_POLICY_REQUEST = 106;
        public static final int ADD_POLICY_RESULT = 107;
        public static final int NOMINEE_DASHBOARD_REQUEST = 108;
        public static final int HELP_REQUEST = 109;
        public static final int HELP_RESULT = 201;
        public static final int PROMOTIONS_REQUEST = 202;
        public static final int CLAIMS_REQUEST = 203;
        public static final int DOCUMENTS_REQUEST = 204;
    }

    public static class Notification {
        public static final String ID = "id";
        public static final String POLICY_NUMBER = "policy_number";
        public static final String TYPE = "type";

        public static final String DUES_CHANNEL_ID = "dues";
        public static final String NOTIFICATION_SHARED_PREFERENCE = "Notifications";

        public static class Type {
            public static final int DAY = 1;
            public static final int WEEK = 2;
            public static final int TWO_WEEKS = 3;
            public static final int MONTH = 4;
            public static final int TWO_MONTHS = 5;
        }
    }

    public static class Query{
        public static final String TYPE = "type";

        public static class Type{
            public static final int CLAIMS = 0;
            public static final int LEGAL_MATTER = 1;
            public static final int CASE_FILING = 2;
            public static final int DISPUTE_HANDLING = 3;
            public static final int BUY_NEW = 4;
            public static final int USAGE = 5;
        }
    }

    public static class Claims {
        public static final String NUMBER = "+91 78271 42153";
    }

    public static class Documents {
        public static final int ADHAAR = 0;
        public static final int PASSPORT = 1;
        public static final int PAN = 2;
        public static final int VOTER_ID = 3;
        public static final int DRIVING_LICENSE = 4;
        public static final int RATION_CARD = 5;

        public static final int NUMBER = 6;

        public static final String IMAGE_DIRECTORY = "images";
        public static final long ONE_MEGABYTE = 1024*1024;
        public static final String[] NAMES = {"adhaar", "passport", "pan", "voter", "driving", "ration"};
    }
}
