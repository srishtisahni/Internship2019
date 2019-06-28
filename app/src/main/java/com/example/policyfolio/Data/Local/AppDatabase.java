package com.example.policyfolio.Data.Local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.policyfolio.Data.Local.Classes.Documents;
import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Data.Local.Classes.InsuranceProducts;
import com.example.policyfolio.Data.Local.Classes.InsuranceProvider;
import com.example.policyfolio.Data.Local.Classes.Nominee;
import com.example.policyfolio.Data.Local.Classes.Policy;
import com.example.policyfolio.Data.Local.Classes.User;

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
