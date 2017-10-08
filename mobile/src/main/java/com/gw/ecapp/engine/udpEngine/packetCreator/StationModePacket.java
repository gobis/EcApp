package com.gw.ecapp.engine.udpEngine.packetCreator;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gobi on 08/10/17.
 */

public class StationModePacket {

    @SerializedName("DeviceName")
    public String mDeviceName;

    @SerializedName("ssid")
    public String mSsid;

    @SerializedName("password")
    public String mPassword;


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
