package com.gw.ecapp.engine.udpEngine;

import com.gw.ecapp.AppConfig;

import java.util.regex.Pattern;

/**
 * Created by iningosu on 8/25/2017.
 */

public class EngineUtils {

    // UDP engine related constants
   // public static InetAddress UDP_UNI_CAST_IP;
    public static int UDP_UNI_CAST_PORT = 4210;
    public static String UDP_UNI_CAST_IP = AppConfig.DEVICE_IP;

    public static final int UDP_MULTI_CAST_PORT = 5666;


    public static final int MAX_MSG_LENGTH = 1024;
    public static final int UDP_TIMEOUT = 5000;   // time out in milli sec

    public static final int UNI_CAST_MAX_RETRY_COUNT = 3;   // uni cast retry count

    public static final long WIFI_CONN_WAIT_TIME = 25 * 1000;

    public static final String SSID = "SSID";
    public static final String WIFI_LEVEL = "WifiLevel";


    public static void setUdpUniCastIp(String inetAddress, int portNumber){
       /* UDP_UNI_CAST_IP = inetAddress;
        UDP_UNI_CAST_PORT = portNumber;*/
    }









}
