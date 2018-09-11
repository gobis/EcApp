package com.gw.ecapp.devicecontrol.stationmode;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.packetCreator.RestartMsgPacket;
import com.gw.ecapp.engine.udpEngine.packetCreator.StationModePacket;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPRequestStatus;
import com.gw.ecapp.storage.AppPreferences;

import static com.gw.ecapp.AppConfig.ST_MODE_INTERVAL;

/**
 * Created by Gobi on 08/10/17.
 */

public class StationModeHelper implements UDPRequestStatus{


    String TAG = getClass().getSimpleName();

    private Handler mUiHandler;
    private Context mContext;
    private StationModeInterface mStModeInterface;

    private void setStationModeInterface(StationModeInterface stationModeInterface){
        mStModeInterface = stationModeInterface ;
    }

    public void setDeviceInStationMode(Context context){

        mContext = context ;

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


    public void restartDevice(Context context){

        mContext = context ;

        UDPClient mEngine = (UDPClient) CommEngine.getCommsEngine(context, CommEngine.ENGINE_TYPE.UDP,this);

        RestartMsgPacket restartPacket = new RestartMsgPacket();

        Gson gson = new Gson();
        String restartPacketData = gson.toJson(restartPacket);
        mEngine.sendMessageToDevice(restartPacketData);
    }

    /**
     * send station mode timer
     * @param packet
     */
    public void sendStationModeRequest(StationModePacket packet){

        Gson gson = new Gson();
        String stationModePacketData = gson.toJson(packet);

        UDPClient mEngine = (UDPClient) CommEngine.getCommsEngine(mContext, CommEngine.ENGINE_TYPE.UDP,this);
        Log.i(TAG," station mode packet data is " + stationModePacketData);
        mEngine.sendMessageToDevice(stationModePacketData);
        stationModeTimerStart();
    }

    /**
     * start timer once you sent the st mode request
     */
    private void stationModeTimerStart(){

        CountDownTimer mConnTimer = new CountDownTimer(ST_MODE_INTERVAL, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "seconds remaining: " + millisUntilFinished / 1000);
                mStModeInterface.stationModeStatus(StationModeStatus.ST_MODE_WORKING);

            }

            @Override
            public void onFinish() {
                stationModeTimeOut();
            }
        };

        mConnTimer.start();

    }

    private void stationModeTimeOut(){
        Log.i(TAG, "Station mode timer completed");
        mStModeInterface.stationModeStatus(StationModeStatus.ST_MODE_TIMEOUT);
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



