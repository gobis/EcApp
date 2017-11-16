package com.gw.ecapp.engine.udpEngine.packetCreator;

import com.google.gson.annotations.SerializedName;
import com.gw.ecapp.AppConstant;

/**
 * Created by iningosu on 8/25/2017.
 */

public class CommandMsgPacket extends Message {

    @SerializedName("RelayName")
    public String mRelayName;

    @SerializedName("RelayStatus")
    public String mRelayStatus;

    public CommandMsgPacket(){
          super(AppConstant.DEVICE_STATUS_COMMAND_NAME);
    }


    public String getRelayName() {
        return mRelayName;
    }

    public void setRelayName(String relayName) {
        this.mRelayName = relayName;
    }

    public String getRelayStatus() {
        return mRelayStatus;
    }

    public void setRelayStatus(String relayStatus) {
        this.mRelayStatus = relayStatus;
    }
}
