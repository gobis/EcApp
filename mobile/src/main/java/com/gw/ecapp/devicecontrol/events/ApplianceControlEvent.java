package com.gw.ecapp.devicecontrol.events;

import com.gw.ecapp.storage.model.DeviceModel;

/**
 * Created by iningosu on 10/21/2017.
 */

public class ApplianceControlEvent {

    public String mMessage;

    private DeviceModel mDeviceModel;

    public ApplianceControlEvent(String message , DeviceModel deviceModel){
        mMessage = message;
        mDeviceModel = deviceModel;
    }

    public DeviceModel getDeviceModel() {
        return mDeviceModel;
    }
}
