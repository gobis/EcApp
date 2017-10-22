package com.gw.ecapp.storage;

import android.content.Context;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;


/**
 * The Class DatabaseManager.
 */
public class DatabaseManager {

    /**
     * Singleton mInstance
     */
    private static DatabaseManager mInstance;

    /**
     * Reference to db helper
     */
    private DataBaseHelper mDbHelper;

    /**
     * Reference to context
     */
    private Context mContext;

    /**
     * Instantiates a new database manager.
     *
     * @param context the context
     */
    private DatabaseManager(Context context) {
        mDbHelper = new DataBaseHelper(context);
        mContext = context;
    }

    /**
     * Gets the single mInstance of DatabaseManager.
     *
     * @param context the context
     * @return single mInstance of DatabaseManager
     */
    public static DatabaseManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseManager(context);
        }
        return mInstance;
    }


    // create curd operation for device model

    public Single<Boolean> insertOrUpdateDevice(final DeviceModel deviceModel) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Dao<DeviceModel, String> deviceDao = mDbHelper.getDaoDevice();
                try {
                    CreateOrUpdateStatus status = deviceDao.createOrUpdate(deviceModel);
                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(status.isCreated() || status.isUpdated());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Single<Boolean> bulkInsertOrUpdateDevice(final List<DeviceModel> deviceModels) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Dao<DeviceModel, String> deviceDao = mDbHelper.getDaoDevice();
                try {
                    CreateOrUpdateStatus status = null;

                    for (DeviceModel deviceModel : deviceModels) {
                        status = deviceDao.createOrUpdate(deviceModel);
                    }

                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(status.isCreated() || status.isUpdated());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Single<Boolean> bulkUpdateDevice(final List<DeviceModel> deviceModels) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Dao<DeviceModel, String> deviceDao = mDbHelper.getDaoDevice();
                try {
                   int rowsAffected = 0 ;

                    for ( int i =0 ; i <  deviceModels.size() ; i++) {

                        // update where MAC ID and relay Number
                        UpdateBuilder<DeviceModel, String> updateBuilder = deviceDao.updateBuilder();
                        Where<DeviceModel, String> where = updateBuilder.where();
                        where.eq(AppConstant.DBField.RELAY_ID, String.valueOf(i+1));
                        where.and();
                        where.eq(AppConstant.DBField.MAC_ID, deviceModels.get(i).getMacId());

                        updateBuilder.updateColumnValue(AppConstant.DBField.APPLIANCE_NAME_ID,deviceModels.get(i).getApplianceName());
                        updateBuilder.updateColumnValue(AppConstant.DBField.DEVICE_NAME_ID,deviceModels.get(i).getDeviceName());
                        rowsAffected = updateBuilder.update();
                    }

                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(rowsAffected > 0);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Single<List<DeviceModel>> getDeviceList() {
        return Single.create(new SingleOnSubscribe<List<DeviceModel>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<List<DeviceModel>> emitter) throws Exception {

                try {
                    Dao<DeviceModel, String> deviceDao = mDbHelper.getDaoDevice();
                    List<DeviceModel> deviceList = deviceDao.queryForAll();


                  /*  Dao<ApplianceModel, Integer> applianceDao = mDbHelper.getDaoAppliance();
                    List<ApplianceModel> applianceList = applianceDao.queryForAll();

                    for ( DeviceModel deviceModel:deviceList ) {

                        String macId = deviceModel.getMacId();

                        ArrayList<ApplianceModel> applianceModelList = new ArrayList<ApplianceModel>();

                        for (ApplianceModel applianceModel:applianceList) {

                            if(macId.equalsIgnoreCase(applianceModel.getDeviceMacId())){
                                applianceModelList.add(applianceModel);
                            }
                        }

                        deviceModel.setConnectedDevices(applianceModelList);
                    }*/



                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(deviceList);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Single<Boolean> deleteDevice(final DeviceModel favorites) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Dao<DeviceModel, String> deviceDao = mDbHelper.getDaoDevice();
                try {
                    int status = deviceDao.delete(favorites);
                    if (!emitter.isDisposed()) {
                        if (status > 0) {
                            emitter.onSuccess(true);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> deleteDevice(final String deviceId) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Dao<DeviceModel, String> deviceDao = mDbHelper.getDaoDevice();
                try {
                    int status = deviceDao.deleteById(deviceId);
                    if (!emitter.isDisposed()) {
                        if (status > 0) {
                            emitter.onSuccess(true);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Single<DeviceModel> getDevice(final String id) {
        return Single.create(new SingleOnSubscribe<DeviceModel>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<DeviceModel> emitter) throws Exception {
                try {
                    Dao<DeviceModel, String> favoriteDao = mDbHelper.getDaoDevice();
                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(favoriteDao.queryForId(id));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    // create curd operation for device model

    public Single<Boolean> insertOrUpdateAppliance(final ApplianceModel applianceModel) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Dao<ApplianceModel, Integer> applianceDao = mDbHelper.getDaoAppliance();
                try {
                    CreateOrUpdateStatus status = applianceDao.createOrUpdate(applianceModel);
                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(status.isCreated() || status.isUpdated());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> bulkInsertOrUpdateAppliance(final List<ApplianceModel> applianceModels) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Dao<ApplianceModel, Integer> applianceDao = mDbHelper.getDaoAppliance();
                try {
                    CreateOrUpdateStatus status = null;
                    for (ApplianceModel applianceModel:applianceModels) {
                        status = applianceDao.createOrUpdate(applianceModel);
                    }

                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(status.isCreated() || status.isUpdated());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


}
