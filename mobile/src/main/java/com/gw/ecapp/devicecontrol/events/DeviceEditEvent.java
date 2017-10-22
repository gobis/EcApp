package com.gw.ecapp.devicecontrol.events;

import com.gw.ecapp.storage.model.DeviceModel;

/**
 * Created by iningosu on 10/22/2017.
 */

public class DeviceEditEvent {

    public DeviceModel mDeviceModel;

    public DeviceEditEvent( DeviceModel deviceModel){
        mDeviceModel = deviceModel;
    }

    public DeviceModel getDeviceModel() {
        return mDeviceModel;
    }
}
