package com.gw.ecapp;

/**
 * Created by iningosu on 9/10/2017.
 */

public class AppConfig {

    public static  boolean DeviceFilter = true ;

    public static final String GATEWAY_PREFIX = "GKWAVE";

    public  static  String DEVICE_IP = "192.168.4.1";

    public static boolean PWD_MAN_ENTRY = true ;

    public static int QRCORE_TRY_INTERVAL  =  2000 ; // 2 Sec

    public static boolean IS_CLOUD_SUPPORTED = false ;

    public static final int NETWORK_SNIFF_INTERVAL = 1000;  // 0.2 Sec
    public static final int NETWORK_SNIFF_PARALLELISM = 10;  // parallelism is 10


}
