package com.gw.ecapp.engine.udpEngine.parser;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iningosu on 9/10/2017.
 */

public class CpuInfoResponse {

     @SerializedName("ModelName")
     private String mModelName;

     @SerializedName("ModelNumber")
     private String mCpuInfo;

     @SerializedName("DeviceName")
     private String mDeviceName;

     @SerializedName("Ssid")
     private String mSsid;

     @SerializedName("Password")
     private String mPassword;

     @SerializedName("MacAddress")
     private String mMacAddress;

     @SerializedName("IpAddress")
     private String mIpAddress;





}
