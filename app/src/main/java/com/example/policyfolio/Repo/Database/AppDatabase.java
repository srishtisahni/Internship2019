package com.example.policyfolio.Repo.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.InsuranceProducts;
import com.example.policyfolio.DataClasses.InsuranceProvider;
import com.example.policyfolio.DataClasses.Nominee;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;

@Database(entities = {
            User.class,
            Policy.class,
            InsuranceProvider.class,
            InsuranceProducts.class,
            Nominee.class
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
