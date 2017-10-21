package com.gw.ecapp.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database operations.
 */
class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    String TAG = getClass().getSimpleName();

    /**
     * The Constant DATABASE_VERSION.
     */
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "ecdb";

    private Dao<DeviceModel, String> daoDevice = null;
    private Dao<ApplianceModel, Integer> daoAppliance = null;

    /**
     * Instantiates a new data base helper.
     *
     * @param context the context
     */
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void clearDataBase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onCreate(android
     * .database.sqlite.SQLiteDatabase,
     * com.j256.ormlite.support.ConnectionSource)
     */
    @Override
    public void onCreate(SQLiteDatabase sqLitDb,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource,
                    DeviceModel.class);
            Log.i(TAG, "Tables are created");
        } catch (java.sql.SQLException e) {
            Log.i(TAG, "error creation on table ");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLitDb, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {

        Log.d(DataBaseHelper.class.getName(), "Upgrading DB from version: " + oldVersion + " to version: " + newVersion);
        if (oldVersion == 1) {
            // handle your db upgrade here
        }
    }


    public Dao<DeviceModel, String> getDaoDevice() {
        if (daoDevice == null) {
            try {
                daoDevice = getDao(DeviceModel.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }

        }
        return daoDevice;
    }


    public Dao<ApplianceModel, Integer> getDaoAppliance() {
        if (daoAppliance == null) {
            try {
                daoAppliance = getDao(ApplianceModel.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }

        }
        return daoAppliance;
    }

}
