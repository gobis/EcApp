package com.gw.ecapp.devicecontrol.edit;

import android.content.Context;

import com.google.gson.Gson;
import com.gw.ecapp.AppUtils;
import com.gw.ecapp.devicecontrol.DeviceControlListActivity;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.packetCreator.StationModePacket;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;
import com.gw.ecapp.utility.StationModeModel;

/**
 * Created by Gobi on 11/16/2017.
 */

public class DeviceEditPresenter {

    private Context mContext;

    public DeviceEditPresenter(Context context) {
        mContext = context;
    }


    public void moveToStationMode(StationModeModel stModel , String deviceName) {
        UDPClient mEngine = (UDPClient) CommEngine.getCommsEngine(mContext, CommEngine.ENGINE_TYPE.UDP);

        StationModePacket stationModePacket = new StationModePacket();
        stationModePacket.setDeviceName(deviceName);
        stationModePacket.setSsid(stModel.getSsid());
        stationModePacket.setPassword(stModel.getPassword());

        String jsonData = AppUtils.getJsonFromObject(stationModePacket);
        mEngine.sendMessageToDevice(jsonData);

    }


}
