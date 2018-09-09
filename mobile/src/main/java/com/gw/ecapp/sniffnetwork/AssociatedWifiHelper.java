package com.gw.ecapp.sniffnetwork;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.gw.ecapp.AppConfig;
import com.gw.ecapp.NetworkUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private boolean isSniffCompleted ;

    // MAC as Key and IP address as value here
    private ConcurrentHashMap<String, String> macAddressMap;
    private List<String> macList;
    private List<String> smartIpList;
    String TAG = getClass().getSimpleName();

    public AssociatedWifiHelper(Context context , List<String> macIds, List<String> smartIpList) {
        mExecutor = Executors.newFixedThreadPool(AppConfig.NETWORK_SNIFF_PARALLELISM);
        mContext = context;
        prepareIpWithMask();
        macAddressMap = new ConcurrentHashMap<>();
        macList = macIds;
        this.smartIpList = smartIpList;
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
    public void startSniffingNetwork() {

        // inform to client about starting of sniffing
        ((NetworkSniffStatus) mContext).sniffStarted(ipMask);
        isSniffCompleted = false ;

        ArrayList<Future> futureTasks = new ArrayList<>();
        try {

            if (smartIpList.size() > 0 && AppConfig.SNIFF_STRATEGY == 0) {
                for (String smartIp : smartIpList) {
                    futureTasks.add(mExecutor.submit(new NwSniffTask(smartIp)));
                }
            } else {
                for (int i = 1; i <= 254; i++) {
                    final String testIp = ipMask + String.valueOf(i);
                    futureTasks.add(mExecutor.submit(new NwSniffTask(testIp)));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception => " + e);
        } finally {
            // inform to client about completion of sniffing
            terminateExecutor();
           // ((NetworkSniffStatus) mContext).sniffCompleted(macAddressMap);
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
        String result = "";

        if (isSniffCompleted) {
            // no need to proceed once sniff completed
            return result;
        }
        try {
            Process p1 = Runtime.getRuntime().exec("ping -c 1 " + ip);
            int returnVal = p1.waitFor();

            boolean reachable = (returnVal == 0);
            if (reachable) {
                //currentHost (the IP Address) actually exists in the network
                Log.i(TAG, ip + " is reachable using ping");

                String macAddress = getMacFromArpCache(ip);
                if (macAddress != null) {
                    result = macAddress + "," + ip;
                    macAddressMap.putIfAbsent(macAddress.toLowerCase(), ip);
                }
            } else {
                Log.i(TAG, ip + " is not reachable using ping");
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception occurred while sniffing IP => " + ip);
        } finally {

        }
        checkForSniffCompletion(ip);
        return result;
    }

    private void checkForSniffCompletion(String ip) {

        String[] splitIp = ip.split("\\.");
        String last = splitIp[3];

        int lastDigit = Integer.parseInt(last);

        // strategy 1: when all IP completes
        if (lastDigit > 252 && !isSniffCompleted) {
            isSniffCompleted = true;
            ((NetworkSniffStatus) mContext).sniffCompleted(macAddressMap);
        }

        // strategy 2: when we found all mac Ids
        if (!isSniffCompleted && foundAllMacIds()) {
            isSniffCompleted = true;
            ((NetworkSniffStatus) mContext).sniffCompleted(macAddressMap);
        }
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
     * terminating executor
     * called when all the given mac Ids are found
     */
    private void terminateExecutor() {
        Log.i(TAG, "Terminating executors");
        mExecutor.shutdown();
    }


    private String getMacFromArpCache(String ip) {
        if (ip == null)
            return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0])) {
                    // Basic sanity check
                    String mac = splitted[3];
                    Log.i(TAG, "mac address from arp table is " + mac);
                    if (mac.matches("..:..:..:..:..:..")) {
                        return mac;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean foundAllMacIds() {
        boolean foundAll = true;
        if(macList == null || macList.size() == 0 ){
            return false;
        }
        for (String mac : macList) {
            boolean found = macAddressMap.containsKey(mac.toLowerCase()) ;
            if (!found) {
                foundAll = false;
                break;
            }
        }
        return foundAll;
    }

    /**
     * get network sniff result
     *
     * @return
     */
    public ConcurrentHashMap<String, String> getNetworkSniffResult() {
        return macAddressMap;
    }

    public interface NetworkSniffStatus {
        void sniffStarted(String maskIp);
        void sniffCompleted(ConcurrentHashMap<String, String> map);
    }

}
