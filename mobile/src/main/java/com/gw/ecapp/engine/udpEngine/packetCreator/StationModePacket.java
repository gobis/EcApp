package com.gw.ecapp.engine.udpEngine.packetCreator;

import com.google.gson.annotations.SerializedName;
import com.gw.ecapp.AppConstant;

/**
 * Created by Gobi on 08/10/17.
 */

public class StationModePacket extends Message{

    @SerializedName("DeviceName")
    public String mDeviceName;

    @SerializedName("ssid")
    public String mSsid;

    @SerializedName("password")
    public String mPassword;


    public StationModePacket(){
        super(AppConstant.STATION_COMMAND_NAME);
    }


    public void setDeviceName(String deviceName){
        mDeviceName = deviceName;
    }


    public String getDeviceName(){
        return mDeviceName ;
    }

    public void setSsid(String ssid){
        mSsid = ssid ;
    }

    public String getSsid(){
        return mSsid ;
    }

    public void setPassword(String password){
        mPassword = password ;
    }

    public String getPassword(){
        return mPassword ;
    }







}
