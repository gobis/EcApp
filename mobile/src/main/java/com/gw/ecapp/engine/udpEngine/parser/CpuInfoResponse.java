package com.gw.ecapp.engine.udpEngine.parser;

import com.google.gson.annotations.SerializedName;
import com.gw.ecapp.engine.udpEngine.packetCreator.Message;

/**
 * Created by iningosu on 9/10/2017.
 */

public class CpuInfoResponse extends Message{

    public CpuInfoResponse(){

    }

     @SerializedName("Model Name")
     private String mModelName;

     @SerializedName("Model No")
     private String mCpuInfo;

     @SerializedName("DeviceName")
     private String mDeviceName;

     @SerializedName("ssid")
     private String mSsid;

     @SerializedName("password")
     private String mPassword;

     @SerializedName("Mac address")
     private String mMacAddress;

     @SerializedName("IPaddress")
     private int[] mIpAddress;


    public String getModelName() {
        return mModelName;
    }

    public void setModelName(String modelName) {
        this.mModelName = modelName;
    }

    public String getCpuInfo() {
        return mCpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        this.mCpuInfo = cpuInfo;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        this.mDeviceName = deviceName;
    }

    public String getSsid() {
        return mSsid;
    }

    public void setSsid(String ssid) {
        this.mSsid = ssid;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public void setMacAddress(String mMacAddress) {
        this.mMacAddress = mMacAddress;
    }

    public int[] getmIpAddress() {
        return mIpAddress;
    }

    public void setmIpAddress(int[] mIpAddress) {
        this.mIpAddress = mIpAddress;
    }

    @Override
     public String toString() {
          return "CpuInfoResponse{" +
                  "mModelName='" + mModelName + '\'' +
                  ", mCpuInfo='" + mCpuInfo + '\'' +
                  ", mDeviceName='" + mDeviceName + '\'' +
                  ", mSsid='" + mSsid + '\'' +
                  ", mPassword='" + mPassword + '\'' +
                  ", mMacAddress='" + mMacAddress + '\'' +
                  '}';
     }
}
