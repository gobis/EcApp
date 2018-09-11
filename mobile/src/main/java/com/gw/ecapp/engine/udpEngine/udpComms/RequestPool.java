package com.gw.ecapp.engine.udpEngine.udpComms;

import com.gw.ecapp.engine.udpEngine.packetCreator.Message;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by gobi on 12/31/2017.
 * responsible for adding request in the queue and remove successful request from the queue
 */

class RequestPool {

    private ArrayList<Message> mRequestDataList;
    private Gson gson;

    private static final RequestPool ourInstance = new RequestPool();

    static RequestPool getInstance() {
        return ourInstance;
    }

    private RequestPool() {
        mRequestDataList = new ArrayList<>();
        gson = new Gson();
    }

    /**
     * responsible to add the request
     * @param message
     */
    public void addRequestToQueue(Message message){
        if(null != message){
            mRequestDataList.add(message);
        }
    }

    /**
     * responsible to remove the processed request from the pool
     * @param response
     */
    public void removeRequestFromQueue(String response){
        Message msg = gson.fromJson(response, Message.class);
        for (Message message : mRequestDataList) {
            if (message.equals(msg)) {
                mRequestDataList.remove(message);
                break;
            }
        }
    }


}
