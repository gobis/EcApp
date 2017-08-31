package com.gw.ecapp.engine;

import com.gw.ecapp.engine.udpEngine.packetCreator.Message;

/**
 * Created by iningosu on 8/26/2017.
 */

public interface CommsInterface {

   void sendData(Message msg);

}
