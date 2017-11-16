package com.gw.ecapp;

/**
 * Created by iningosu on 10/15/2017.
 */

public class AppConstant {

    public static class Extras {

        public static String Device = "Device";
        public static String DEVICE_SSID = "DeviceSsid";
        public static String DEVICE_PWD = "DevicePassword";
        public static String SCAN_LAN = "ScanLan";

    }


    public static class DBField {
        public static String RELAY_ID = "relayNumber";
        public static String MAC_ID = "macId";
        public static String DEVICE_NAME_ID = "deviceName";
        public static String APPLIANCE_NAME_ID = "applianceName";

    }

    public static String ENTER_PWD_MAN= "manual";
    public static String ENTER_PWD_SCAN= "scan";

    public static int AP_MODE = 0 ;
    public static int STATION_MODE = 1 ;
    public static int UNKNOWN_MODE = 2 ;

    // Commands name
    public static String STATION_COMMAND_NAME = "st_command";
    public static String CPU_INFO_COMMAND_NAME = "cpuinfo";
    public static String DEVICE_STATUS_COMMAND_NAME = "device_status";

}
