package com.gw.ecapp.engine;

import android.content.Context;

import com.google.gson.Gson;
import com.gw.ecapp.engine.udpEngine.packetCreator.Message;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;

import java.util.ArrayList;

/**
 * Created by iningosu on 8/26/2017.
 */

public class CommEngine implements CommsInterface{


    private static CommEngine mCommsEngine;
    private Context mContext;

    public enum ENGINE_TYPE{
        UDP,
        CLOUD
    }

    public static CommEngine getCommsEngine(Context context , ENGINE_TYPE type){
       switch (type){
           case UDP:
               mCommsEngine = UDPClient.getInstance(context);
               break;
           case CLOUD:
               break;


       }
        return mCommsEngine;
    }


    /**
     * default constructor as this is base class
     */
    public CommEngine(){

    }

    @Override
    public void sendData(Message msg) {

    }
}
