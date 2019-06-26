package com.example.policyfolio.Repo.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.policyfolio.Repo.Database.DataClasses.Documents;
import com.example.policyfolio.Repo.Database.DataClasses.Notifications;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProducts;
import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;

//Room Database to store everything Locally
@Database(entities = {
            User.class,
            Policy.class,
            InsuranceProvider.class,
            InsuranceProducts.class,
            Nominee.class,
            Notifications.class,
            Documents.class
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
