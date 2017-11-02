package com.gw.ecapp.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import com.gw.ecapp.AppConfig;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Gobi on 08/10/17.
 */

public class AssociatedWifiHelper {


    private ExecutorService mExecutor;
    private Context mContext;
    private Network mWifiNetwork;

    private String ipMask;

    private List<String> mMacIdList;


    private int mDeviceCount;

    private ConcurrentHashMap<String,Boolean> macAddressMap;

    String TAG = getClass().getSimpleName();

    public void getAssociatedWifi(Context context) {
        mExecutor = Executors.newFixedThreadPool(AppConfig.NETWORK_SNIFF_PARALLELISM);
        mContext = context;
        prepareIpWithMask();

        macAddressMap = new ConcurrentHashMap<>();
    }

    /**
     * setting mac id
     * @param macIds
     */
    public void setMacIds(List<String> macIds){
        mMacIdList = macIds;
        mDeviceCount = 0;

        for (String macId:macIds) {
            macAddressMap.put(macId,false);
        }
        startSniffingNetwork();
    }

    /**
     * prepare IP with mask for reachablity
     */
    private void prepareIpWithMask() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null ;
        checkForNetworkConnection();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(getClass().getSimpleName(), " Binding to Wifi Network ");
            if (null != mWifiNetwork) {
                activeNetwork = cm.getNetworkInfo(mWifiNetwork);
            }
        }else {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        WifiInfo connectionInfo = wm.getConnectionInfo();
        int ipAddress = connectionInfo.getIpAddress();
        String ipString = Formatter.formatIpAddress(ipAddress);

        Log.d(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
        Log.d(TAG, "ipString: " + String.valueOf(ipString));

        ipMask = ipString.substring(0, ipString.lastIndexOf(".") + 1);

        Log.d(TAG, "prefix: " + ipMask);

    }

    /**
     * start sniffing the network
     */
    private void startSniffingNetwork() {
        try {
            for (int i = 0; i < 255; i++) {
                String testIp = ipMask + String.valueOf(i);
                Future<String> future = mExecutor.submit(new NwSniffTask(testIp));

            }
        }catch (Exception e){

        }finally {

        }
    }


    /**
     * class responsible for sending and receiving data
     */
    private class NwSniffTask implements Callable<String> {

        private String mIP;

        public NwSniffTask(String ip) {
            mIP = ip;
        }

        @Override
        public String call() throws Exception {
            return  checkCurrentIP(mIP);
        }
    }

    /**
     * checking the given IP is reachable or not , and getting the mac  ID for the same
     * @param ip
     * @return
     */
    private String checkCurrentIP(String ip){

        String macAddress = null;
        try {
            InetAddress address = InetAddress.getByName(ip);
            boolean reachable = address.isReachable(AppConfig.NETWORK_SNIFF_INTERVAL);
            if (reachable) {
                NetworkInterface nwInterface = NetworkInterface.getByInetAddress(address);
                if (null != nwInterface) {
                    byte[] mac = nwInterface.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < mac.length; j++) {
                        sb.append(String.format("%02X%s", mac[j], (j < mac.length - 1) ? "-" : ""));
                    }
                    System.out.println("Current MAC address : " + sb.toString());
                    macAddress = sb.toString();

                    checkForExecutorTermination(macAddress);

                }
            }
        }catch (Exception e){
            Log.e(TAG, " Exception " + e.toString());
        }
        return macAddress;
    }


    public void checkForNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (AppConfig.IS_CLOUD_SUPPORTED) {
            // get active network only


        } else {
            Network[] networks = connectivityManager.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    mWifiNetwork = network; // Grabbing the Network object for later usage
                }
            }
        }
    }

    /**
     * checking the given mac id and taking call on terminating the executors
     * @param macAddress
     */
    private void checkForExecutorTermination(String macAddress){

        if(mMacIdList.contains(macAddress)){
            mDeviceCount++;
        }

        if(mMacIdList.size() == mDeviceCount){
            terminateExecutor();
        }

    }


    /**
     * terminating executor
     * called when all the given mac Ids are found
     */
    private void terminateExecutor(){
        mExecutor.shutdown();
    }


}
