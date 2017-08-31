package com.gw.ecapp;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.gw.ecapp.engine.udpEngine.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by iningosu on 8/27/2017.
 */

public class NetworkUtils {

    /**
     * This function filters for the Access point having prefix "RFS"
     *
     */
    public static boolean filterAccessPointsForGateway(String name) {
        name = name.toUpperCase();
        return name.startsWith(AppUtils.GATEWAY_PREFIX);
    }



    public static ArrayList<HashMap<String, String>> getWifiAccessPointsList(
            boolean isFilterByGateway, Context context) {
        ArrayList<HashMap<String, String>> wifiArrayList = new ArrayList<>();
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean scan = mWifiManager.startScan();
        if (scan) {
            // assigning the scan results to List
            List<ScanResult> scanResults = mWifiManager.getScanResults();

            int size = scanResults.size();
            int maxLevel = 5;
            for (int i = 0; i <= size - 1; i++) {
                // filtering the scan results based on ServiceSetID
                if (scanResults.get(i).SSID != null) {
                    int level = WifiManager.calculateSignalLevel(scanResults.get(i).level, maxLevel);
                    String serviceSetID = scanResults.get(i).SSID;
                    if (!isFilterByGateway || NetworkUtils.filterAccessPointsForGateway(serviceSetID)) {
                        HashMap<String, String> item = new HashMap<>();
                        item.put(AppUtils.SSID, serviceSetID);
                        item.put("Column", scanResults.get(i).capabilities);
                        item.put(AppUtils.WIFI_LEVEL, String.valueOf(level));
                        item.put("WifiNameColor", "0");
                        item.put("WifiConnectionState", "0");
                        wifiArrayList.add(item);
                    }
                }
            }
        }
        return wifiArrayList;
    }
}
