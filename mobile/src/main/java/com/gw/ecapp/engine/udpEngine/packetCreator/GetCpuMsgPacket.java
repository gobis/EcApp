package com.gw.ecapp.engine.udpEngine.packetCreator;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iningosu on 8/25/2017.
 */

public class GetCpuMsgPacket extends Message {

    @SerializedName("cpuInfo")
    private String mCpuInfo;

    public GetCpuMsgPacket(){
        super();
        mCpuInfo = "";
    }


    public String getCpuInfo() {
        return mCpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        mCpuInfo = cpuInfo;
    }

}
