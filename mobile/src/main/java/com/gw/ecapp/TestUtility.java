package com.gw.ecapp;

import android.content.Context;
import android.util.Log;

import com.gw.ecapp.storage.DatabaseManager;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by iningosu on 10/9/2017.
 */

public class TestUtility {


    String TAG = getClass().getSimpleName();

    // test database
    DeviceModel deviceModel;

    public void TestDBInsert(final Context context){

        deviceModel = new DeviceModel();

        deviceModel.setDeviceName("Test1");
        deviceModel.setChannelCount(4);
        deviceModel.setConfigureName("User Hall");
        deviceModel.setmLastConnectedIP("192.168.1.6");
        deviceModel.setmPreferredIP("192.168.1.10");
        deviceModel.setMacId("40-od-re-34-d3");


        DatabaseManager dbManager = DatabaseManager.getInstance(context);
        dbManager.insertOrUpdateDevice(deviceModel)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        Log.i(TAG, " insertion/ update status " + aBoolean);

                        ApplianceModel applianceModel = new ApplianceModel();
                        applianceModel.setDeviceName("Bulb");
                        applianceModel.setRelayNumber("1");
                        applianceModel.setDeviceMacId(deviceModel.getMacId());
                        insertAppliance(context,applianceModel);

                        ApplianceModel applianceModel2 = new ApplianceModel();
                        applianceModel2.setDeviceName("Fan");
                        applianceModel.setDeviceMacId(deviceModel.getMacId());
                        applianceModel2.setRelayNumber("2");

                        insertAppliance(context,applianceModel);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i(TAG, "Exception in inserting models " + throwable.getMessage());
                    }
                });

    }



    private void insertAppliance(final Context context , ApplianceModel applianceModel){

        DatabaseManager dbManager = DatabaseManager.getInstance(context);

        dbManager.insertOrUpdateAppliance(applianceModel)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        Log.i(TAG, " insertion/ update appliance status " + aBoolean);
                        testRead(context);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i(TAG, "Exception in inserting models " + throwable.getMessage());
                    }
                });

    }



    private void testRead(Context context){

        DatabaseManager dbManager = DatabaseManager.getInstance(context);
        dbManager.getDeviceList()
                .subscribe(new Consumer<List<DeviceModel>>() {
                    @Override
                    public void accept(@NonNull List<DeviceModel> deviceModels) throws Exception {

                        for (DeviceModel deviceModel:deviceModels ) {
                            Log.d(TAG, " Device Model data" + deviceModel.toString());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.d(TAG, " Applicance Model fetch failed ");
                    }
                });

    }



}
