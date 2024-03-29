package com.example.policyfolio.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.policyfolio.data.local.classes.Documents;
import com.example.policyfolio.data.local.classes.Notifications;
import com.example.policyfolio.data.local.classes.ProviderTypeRelationship;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.data.local.classes.InsuranceProducts;
import com.example.policyfolio.data.local.classes.InsuranceProvider;
import com.example.policyfolio.data.local.classes.Nominee;
import com.example.policyfolio.data.local.classes.Policy;
import com.example.policyfolio.data.local.classes.User;

//Room Database to store everything Locally
@Database(entities = {
            User.class,
            Policy.class,
            InsuranceProvider.class,
            InsuranceProducts.class,
            Nominee.class,
            Notifications.class,
            Documents.class,
            ProviderTypeRelationship.class
            }, version = Constants.DATABASE_VERSION, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public abstract PolicyFolioDao policyFolioDao();

    public static AppDatabase getInstance(Context context) {
        //Singleton Pattern
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class, Constants.DATABASE_NAME)
                    .build();
        }
        return INSTANCE;
    }

    public void destroyInstance(){
        INSTANCE = null;
    }
}
