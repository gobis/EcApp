package com.gw.ecapp.storage;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

import com.gw.ecapp.storage.roomdbModel.ApplianceDao;
import com.gw.ecapp.storage.roomdbModel.ApplianceModel;
import com.gw.ecapp.storage.roomdbModel.DeviceDao;
import com.gw.ecapp.storage.roomdbModel.DeviceModel;

/**
 * Created by iningosu on 11/4/2017.
 */

@Database(entities = {DeviceModel.class, ApplianceModel.class}, version = 1)
public abstract class  AppDatabase  extends RoomDatabase {

    public static final String DB_NAME = "ecapp_db";
    public abstract DeviceDao getDeviceDao();
    public abstract ApplianceDao getApplianceDao();

}
