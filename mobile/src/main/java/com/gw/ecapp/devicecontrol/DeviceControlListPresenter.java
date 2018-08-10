package com.gw.ecapp.devicecontrol;

import android.bluetooth.BluetoothClass;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.AppUtils;
import com.gw.ecapp.engine.udpEngine.EngineUtils;
import com.gw.ecapp.storage.model.DeviceModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by iningosu on 9/10/2017.
 */

public class DeviceControlListPresenter {

    public List<DeviceModel> getDevicesAfterSniff(List<DeviceModel> deviceList, ConcurrentHashMap<String, String> mapList) {

        for (DeviceModel device : deviceList) {
            if (!mapList.containsValue(device.getMacId())) {
                device.setConnMode(AppConstant.AP_MODE);
            }else{
                // mac found , device is in station mode
                device.setConnMode(AppConstant.STATION_MODE);
                // setting IP address once identified it 
                EngineUtils.setUdpUniCastIp(mapList.get(device.getMacId()),EngineUtils.UDP_UNI_CAST_PORT);
            }
        }
        return deviceList;
    }


}
