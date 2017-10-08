package com.gw.ecapp.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * Helper class to access data saved in Stored Preferences
 */
public class AppPreferences {

    public final String PREFS_NAME = "EcAppPref";

    public final String DEVICE_INFO = "deviceInfo";

    public final String ROUTER_SSID = "ssid";

    public final String ROUTER_PASSWORD = "password";

    public final String ROUTER_AVAILABLE = "hasRouter";



    // store all the device info , device name , mac address


    private static AppPreferences mInstance;
    private Context mContext;
    private SharedPreferences mAppPref;


    private AppPreferences(Context context) {
        mContext = context;
        mAppPref = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    }

    public static synchronized AppPreferences getInstance(Context context) {
        if (mInstance == null) {
            if (context == null) return null;
            mInstance = new AppPreferences(context);
        }
        return mInstance;
    }

    public static AppPreferences getInstance() {
        return mInstance;
    }

    public String getDeviceInfo() {
        return mAppPref.getString(DEVICE_INFO, "");
    }

    /**
     * store all the device info
     * @param deviceInfo
     */
    public void setDeviceInfo(String deviceInfo) {

        String devices = getDeviceInfo();

        if(!devices.contains(deviceInfo)) {
            deviceInfo = deviceInfo + devices ;
            Log.i("", "info to be persist " + deviceInfo );
            mAppPref.edit().putString(DEVICE_INFO, deviceInfo).commit();
        }else{
            Log.i("",deviceInfo + " already stored");
        }
    }


    public String getRouterSSID() {
        return mAppPref.getString(ROUTER_SSID, "");
    }

    public void setRouterSSID(String ssid) {
        mAppPref.edit().putString(ROUTER_SSID, ssid).commit();
    }

    public String getRouterPassword() {
        return mAppPref.getString(ROUTER_PASSWORD, "");
    }

    public void setRouterPassword(String password) {
        mAppPref.edit().putString(ROUTER_PASSWORD, password).commit();
    }

    public boolean hasRouter() {
        return mAppPref.getBoolean(ROUTER_AVAILABLE, false);
    }

    public void setRouter(boolean hasRouter) {
        mAppPref.edit().putBoolean(ROUTER_AVAILABLE, hasRouter).commit();
    }

    // device
    // tech : mac address , IP address , no.of channels
    // user :  switch mapping



}