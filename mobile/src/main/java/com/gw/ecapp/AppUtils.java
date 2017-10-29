package com.gw.ecapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;
import com.gw.ecapp.engine.udpEngine.packetCreator.Message;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by iningosu on 9/11/2017.
 */

public class AppUtils {

    public static String ORIGIN = "mobile";


    // which mode you want to connect the device
    // control mode => configured in AP mode, and you are trying to control the devices
    // config mode => app is in AP mode , changing everything to station mode
    public static  enum ConnMode{
        AP_MODE,
        CONFIG_MODE,
        STATION_MODE
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    private static final Pattern IP_PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");


    public static boolean validateIp(final String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }



    public static String getJsonFromObject(Message obj){
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        return json;
    }





}
