package com.gw.ecapp;

/**
 * Created by iningosu on 10/15/2017.
 */

public class AppConstant {

    public static String DB_NAME = "ecapp_db";

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
}
