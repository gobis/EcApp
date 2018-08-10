package com.gw.ecapp.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;

import com.gw.ecapp.AppConfig;
import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
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

    // IP as Key and MAC address as value here
    private ConcurrentHashMap<String, String> macAddressMap;

    public static final String NoMac = "00:00:00:00:00:00";

    String TAG = getClass().getSimpleName();

    public AssociatedWifiHelper(Context context) {
        mExecutor = Executors.newFixedThreadPool(AppConfig.NETWORK_SNIFF_PARALLELISM);
        mContext = context;
        prepareIpWithMask();
        macAddressMap = new ConcurrentHashMap<>();
    }

    /**
     * setting mac id
     *
     * @param macIds
     */
    public void setMacIds(List<String> macIds) {
        mMacIdList = macIds;
        Log.i(TAG, "Mac Id list" + macIds.toString());
        mDeviceCount = 0;
        startSniffingNetwork();
    }

    /**
     * prepare IP with mask for reachablity
     */
    private void prepareIpWithMask() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        checkForNetworkConnection();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(getClass().getSimpleName(), " Binding to Wifi Network ");
            if (null != mWifiNetwork) {
                activeNetwork = cm.getNetworkInfo(mWifiNetwork);
            }
        } else {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        WifiInfo connectionInfo = wm.getConnectionInfo();
        int ipAddress = connectionInfo.getIpAddress();
        String ipString = NetworkUtils.intToIP(ipAddress);

        Log.d(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
        Log.d(TAG, "ipString: " + String.valueOf(ipString));

        ipMask = ipString.substring(0, ipString.lastIndexOf(".") + 1);

        Log.d(TAG, "prefix: " + ipMask);

    }

    /**
     * start sniffing the network
     */
    private void startSniffingNetwork() {
        mDeviceCount = 0;

        ((NetworkSniffStatus)mContext).sniffStarted();
        try {
            for (int i = 0; i < 255; i++) {
                final String testIp = ipMask + String.valueOf(i);
                mExecutor.submit(new NwSniffTask(testIp));
            }
        } catch (Exception e) {

        } finally {

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
            return checkCurrentIP(mIP);
        }
    }

    /**
     * checking the given IP is reachable or not , and getting the mac  ID for the same
     *
     * @param ip
     * @return
     */
    private String checkCurrentIP(final String ip) {

        try {

            if(ip.endsWith("254")){
                Log.i(TAG, "reading table after reaching last ip ");
                readTable();
            }
            Process p1 = Runtime.getRuntime().exec("ping -c 1 " + ip);
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            if (reachable) {
                //currentHost (the IP Address) actually exists in the network
                Log.i(TAG, ip + " is reachable using ping");
            } else {
                Log.i(TAG, ip + " is not reachable using ping");
            }
        } catch (Exception e) {

        }

       /* try {
            InetAddress address = InetAddress.getByName(ip);

            boolean reachable = address.isReachable(AppConfig.NETWORK_SNIFF_INTERVAL) ;

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

                Log.i(TAG, ip + " is reachable using reachable API" );
            }else{
                Log.i(TAG, ip + " is not reachable using reachable API" );
            }
        }catch (Exception e){
            Log.e(TAG, " Exception " + e.toString());
        }
        */

        return null;
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
     *
     * @param macAddress
     */
    private void checkForExecutorTermination(String macAddress) {
        Log.i(TAG, "Checking mac address");
        if (null != mMacIdList && mMacIdList.contains(macAddress)) {
            mDeviceCount++;
            if (mMacIdList.size() == mDeviceCount) {
                terminateExecutor();
            }
        }
    }

    /**
     * terminating executor
     * called when all the given mac Ids are found
     */
    private void terminateExecutor() {
        Log.i(TAG, "Terminating executors");
        mExecutor.shutdown();
    }

    private void readTable() {
        try {
            BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            // read the output from the command
            String s = null;

            while ((s = localBufferedReader.readLine()) != null) {
               String[] ipmac = s.split("\\s+");
                if (!ipmac[0].contains("IP")) {
                    String ip = ipmac[0].trim();
                    String mac = ipmac[3].trim();
                    if (!NoMac.equals(mac)) {
                        Log.i(TAG, "IP " + ip + "  mac address " + mac);
                        if (macAddressMap.containsKey(ip)) {
                            macAddressMap.put(ip, mac);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }finally {
            ((NetworkSniffStatus)mContext).sniffCompleted(macAddressMap);
        }
    }


    /**
     * get network sniff result
     * @return
     */
    public ConcurrentHashMap<String,String> getNetworkSniffResult(){
        return macAddressMap;
    }

    public interface NetworkSniffStatus{
        void sniffStarted();
        void sniffCompleted(ConcurrentHashMap<String,String> map);
    }

}
