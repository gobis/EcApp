package com.gw.ecapp.configuration;

import android.content.Context;
import android.util.Log;

import com.gw.ecapp.engine.udpEngine.EngineUtils;
import com.gw.ecapp.storage.DatabaseManager;
import com.gw.ecapp.storage.model.DeviceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iningosu on 11/17/2017.
 */

public class DeviceListPresenter {

    private Context mContext;
    private DeviceListInterface mCallBack;
    private String TAG = getClass().getSimpleName();
    private ArrayList<HashMap<String, String>> mNetworkList;

    public DeviceListPresenter(Context context) {
        mContext = context;
    }

    /**
     * set the call back for the device
     * @param callback
     */
    public void setFilterDeviceCallBack(DeviceListInterface callback){
        mCallBack = callback ;
    }


    /**
     * get filtered device by already configured device
     * @param networkList
     */
    public void getFilteredConfigDevice(ArrayList<HashMap<String, String>> networkList) {

        mNetworkList = networkList;
        getDevicesFromDb();

    }


    /**
     * populate data from database
     */
    private void getDevicesFromDb() {

        DatabaseManager dbManager = DatabaseManager.getInstance(mContext);
        dbManager.getDeviceList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<DeviceModel>>() {
                    @Override
                    public void accept(@NonNull List<DeviceModel> deviceModels) throws Exception {
                        filterByAddedDevice(deviceModels);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i(TAG, "Exception in getting device models " + throwable.getMessage());
                        filterByAddedDevice(null);
                    }
                });
    }

    /**
     * filter the list by already added device
     *
     * @param deviceModelList
     */
    private void filterByAddedDevice(List<DeviceModel> deviceModelList) {

        if (deviceModelList != null && !deviceModelList.isEmpty()) {
            Log.i(TAG, "User already added some device ");
            for (DeviceModel deviceModel : deviceModelList) {

                boolean deviceFound = false;
                int itemPos = -1;

                for (int i = 0; i < mNetworkList.size(); i++) {
                    HashMap<String, String> networkMap = mNetworkList.get(i);
                    if (deviceModel.getDeviceSsid().equalsIgnoreCase(networkMap.get(EngineUtils.SSID))) {
                        deviceFound = true;
                        itemPos = i;
                    }
                }

                if (deviceFound) {
                    if (itemPos != -1) {
                        mNetworkList.remove(itemPos);
                    }
                }
            }
        }

        mCallBack.filteredDeviceList(mNetworkList);
    }


    public interface DeviceListInterface {
        void filteredDeviceList(ArrayList<HashMap<String, String>> networkList);
    }


}
