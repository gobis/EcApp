package com.gw.ecapp.engine.udpEngine.events;

import com.gw.ecapp.engine.udpEngine.packetCreator.Message;

/**
 * Created by iningosu on 10/21/2017.
 */

public class MessageArrivedEvent {

    public final Message message;

    public MessageArrivedEvent(Message message) {
        this.message = message;
    }

}
