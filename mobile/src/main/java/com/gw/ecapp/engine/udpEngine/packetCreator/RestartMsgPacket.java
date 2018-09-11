package com.gw.ecapp.engine.udpEngine.packetCreator;

import com.google.gson.annotations.SerializedName;
import com.gw.ecapp.AppConstant;

public class RestartMsgPacket extends Message {

    @SerializedName("Restart")
    public String mDeviceName;

    public RestartMsgPacket() {
        super(AppConstant.RESTART_COMMAND_NAME);
    }


}
