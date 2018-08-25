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

    // MAC as Key and IP address as value here
    private ConcurrentHashMap<String, String> macAddressMap;

    String TAG = getClass().getSimpleName();

    public AssociatedWifiHelper(Context context) {
        mExecutor = Executors.newFixedThreadPool(AppConfig.NETWORK_SNIFF_PARALLELISM);
        mContext = context;
        prepareIpWithMask();
        macAddressMap = new ConcurrentHashMap<>();
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

        ArrayList<Future> futureTasks = new ArrayList<>();
        try {
            for (int i = 0; i < 254; i++) {
                final String testIp = ipMask + String.valueOf(i);
                futureTasks.add(mExecutor.submit(new NwSniffTask(testIp)));
            }
            // iterate through future group
            for (Future future : futureTasks) {
                if (future.isDone()) {
                    String sniffResult = future.get().toString().trim();
                    // expecting this should result macId and IP
                    if (!sniffResult.isEmpty()) {
                        String[] splitResult = sniffResult.split(",");
                        macAddressMap.putIfAbsent(splitResult[0], splitResult[1]);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception => " + e);
        } finally {
            // inform to client about completion of sniffing
            terminateExecutor();
            ((NetworkSniffStatus) mContext).sniffCompleted(macAddressMap);
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
                    Log.i(TAG, " mac address is from  arp table " + macAddress);

                }
            } else {
                Log.i(TAG, ip + " is not reachable using ping");
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception occurred while sniffing IP => " + ip);
        } finally {

        }
        return result;
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
