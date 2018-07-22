package com.gw.ecapp.configuration;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.gw.ecapp.demo.DemoActivity;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.packetCreator.StationModePacket;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPRequestStatus;
import com.gw.ecapp.storage.AppPreferences;

/**
 * Created by Gobi on 08/10/17.
 */

public class StationModeHelper implements UDPRequestStatus {


    String TAG = getClass().getSimpleName();

    public void setDeviceInStationMode(Context context){

        UDPClient mEngine = (UDPClient) CommEngine.getCommsEngine(context, CommEngine.ENGINE_TYPE.UDP,this);

        boolean hasRouter = AppPreferences.getInstance(context).hasRouter();
        if(hasRouter) {
            String ssid = AppPreferences.getInstance(context).getRouterSSID();
            String pwd = AppPreferences.getInstance(context).getRouterPassword();

            StationModePacket packet = new StationModePacket();
            packet.setSsid(ssid);
            packet.setPassword(pwd);
            packet.setDeviceName("");

            Gson gson = new Gson();
            String stationModePacketData = gson.toJson(packet);

            Log.i(TAG," station mode packet data is " + stationModePacketData);
            mEngine.sendMessageToDevice(stationModePacketData);
          //   mEngine.SendMessage();


        }else{
            Log.i(TAG," user does not have router ");
        }
    }


    /**
     * call back when udp request is successful
     */
    @Override
    public void requestSuccess() {

    }

    /**
     * call back when given requst if timeout
     */
    @Override
    public void requestTimeOut() {

    }

    /**
     * call back for retry count
     *
     * @param retryCount
     */
    @Override
    public void requestRetryCount(int retryCount) {

    }
}



