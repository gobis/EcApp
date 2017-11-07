package com.gw.ecapp;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.facebook.stetho.Stetho;
import com.gw.ecapp.storage.AppDatabase;
import com.gw.ecapp.storage.DatabaseManager;

/**
 * Created by iningosu on 10/9/2017.
 */

public class EcApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, AppConstant.DB_NAME).build();

    }
}
