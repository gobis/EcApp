package com.gw.ecapp.engine.udpEngine.packetCreator;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iningosu on 8/25/2017.
 */

public class  GetCpuMsgPacket extends Message {

    @SerializedName("Cpuinfo")
    private String mCpuInfo;

    public GetCpuMsgPacket(){
        super();
        mCpuInfo = "1";
    }


    public String getCpuInfo() {
        return mCpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        mCpuInfo = cpuInfo;
    }

}
