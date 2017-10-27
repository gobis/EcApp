package com.gw.ecapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import com.gw.ecapp.engine.udpEngine.EngineUtils;

import java.util.List;

/**
 * Created by iningosu on 9/3/2017.
 */

public class WifiConnection {


    public static WifiConnection INSTANCE;


    Context mContext;

    ConnectionStatusInterface mWifiConnCallBack;

    CountDownTimer mConnTimer;

    String TAG = getClass().getSimpleName();


    public static synchronized WifiConnection getInstance(ConnectionStatusInterface callback) {

        if (INSTANCE == null) {
            INSTANCE = new WifiConnection();
        }
        INSTANCE.mWifiConnCallBack = callback;
        return INSTANCE;
    }


    private WifiConnection() {

    }


    /**
     * responsible to connect to the provided network
     *
     * @param context
     * @param serviceSetID ssid
     * @param pwd          password
     * @param force        connect to the network, though network config already available
     */
    public void ConnectToServiceSetID(Context context, String serviceSetID, String pwd, boolean force) {
        try {

            startConnectionTimer();

            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            Log.i(getClass().getSimpleName(), " Creating Connection with " + serviceSetID + " passkey " + pwd);
            WifiConfiguration wc = new WifiConfiguration();
            wc.SSID = "\"" + serviceSetID + "\"";
            wc.preSharedKey = "\"" + pwd + "\"";
            wc.hiddenSSID = true;
            wc.status = WifiConfiguration.Status.ENABLED;

            int netId = checkPreviousConfiguration(context, wc);

            if (force) {
                Log.i(getClass().getSimpleName(), " Making connection forcefully , " +
                        "will remove the network and adding it back with the given credential ");

                boolean status = removeNetworkIfMatches(context, serviceSetID);

                Log.i(getClass().getSimpleName(), " Status of network removal " + status);

                netId = wifi.addNetwork(wc);

                if (netId != -1) {
                    wifi.disconnect();
                    wifi.enableNetwork(netId, true);
                    // wifi.saveConfiguration();
                    wifi.reconnect();

                    Log.i(getClass().getSimpleName(), "Will establish connection soon with " + serviceSetID);

                } else {
                    Log.i(getClass().getSimpleName(), " Unable to create connection with " + serviceSetID);
                }

            } else {
                if (netId != -1) {
                    Log.i(getClass().getSimpleName(), " Network available : making connection");
                    // already we have configuration
                    wifi.disconnect();
                    wifi.enableNetwork(netId, true);
                    wifi.reconnect();

                } else {
                    Log.i(getClass().getSimpleName(), " Network unavailable : creating new one ");
                    // this config is not available, hence creating a new one
                    netId = wifi.addNetwork(wc);

                    if (netId != -1) {
                        wifi.disconnect();
                        wifi.enableNetwork(netId, true);
                        // wifi.saveConfiguration();
                        wifi.reconnect();
                        Log.i(getClass().getSimpleName(), "Will establish connection soon with " + serviceSetID);
                    } else {
                        Log.i(getClass().getSimpleName(), " Unable to create connection with " + serviceSetID);
                    }
                }
            }
        } catch (Exception e) {
            Log.i(getClass().getSimpleName(), "Wifi connection  exception " + e.toString());
            handleExceptionWhileWifiConnection();
        }
    }


    private void handleExceptionWhileWifiConnection() {
        stopReceivingWifiChanges();
        mWifiConnCallBack.ConnectionStatus(ConnStatus.UNKNOWN);
    }


    public void startReceivingWifiChanges(Context context) {
        mContext = context;
        if (null != mContext) {
            Log.i(TAG, " Registering to receive  network status ");
            mContext.registerReceiver(this.myWifiReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }


    public void stopReceivingWifiChanges() {
        if (null != mContext) {
            Log.i(TAG, "Un Registering to receive  network status ");
            mContext.unregisterReceiver(myWifiReceiver);
        }
    }


    private final BroadcastReceiver myWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
          //  NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

            Network[] allNetwork = connectivityManager.getAllNetworks();

            Log.i(TAG," Network count " + allNetwork.length);

            for(int i = 0 ; i< allNetwork.length ; i++){

                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(allNetwork[i]);
                Log.i(TAG," Network Type " +networkInfo.getTypeName() + "  Network state " + networkInfo.getState()
                        + " " +networkInfo.getTypeName());


                // if active network type is wifi
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.i(TAG, " Network current status  " + networkInfo.getState());

                    if (networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                        mWifiConnCallBack.ConnectionStatus(ConnStatus.CONNECTING);
                    } else if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        if(null != mConnTimer) {
                            mConnTimer.cancel();
                        }
                        mWifiConnCallBack.ConnectionStatus(ConnStatus.CONNECTED);
                    } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTING) {

                    } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                        mWifiConnCallBack.ConnectionStatus(ConnStatus.DISCONNECTED);

                    } else if (networkInfo.getState() == NetworkInfo.State.UNKNOWN) {

                    }

                } else  if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    Log.i(TAG, "  network type is DATA");
                    resetWifiReceivers();
                }else{

                    if(networkInfo == null) {
                        Log.i(TAG, " Network  activeNetwork is null  " );
                    }else{
                        Log.i(TAG, "  network type is DATA" );
                    }
                }
            }

        }
    };


    /**
     * remove the current network from list if matches
     *
     * @param selectedSSID
     */
    private boolean removeNetworkIfMatches(Context context, String selectedSSID) {

        boolean removeNwStatus = false;

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        selectedSSID = "\"" + selectedSSID + "\"";

        if (null != selectedSSID) {
            for (WifiConfiguration i : list) {
                if (selectedSSID.equalsIgnoreCase(i.SSID)) {
                    removeNwStatus = wifiManager.removeNetwork(i.networkId);
                    boolean saveStatus = wifiManager.saveConfiguration();
                    Log.i(TAG, "removeNetworkIfMatches removing network from the list Nw:: "
                            + selectedSSID + "  Removal status :: " + removeNwStatus +
                          " Saving config status " + saveStatus);
                }
            }
        }

        return removeNwStatus;

    }


    /**
     * @param context
     * @param wc
     * @return
     */
    private int checkPreviousConfiguration(Context context, WifiConfiguration wc) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configs = wifi.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals(wc.SSID)) return config.networkId;
        }
        return wc.networkId;
    }

    /**
     * start the timer before making connection
     */
    private void startConnectionTimer() {

        mConnTimer = new CountDownTimer(EngineUtils.WIFI_CONN_WAIT_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                stopReceivingWifiChanges();
                mWifiConnCallBack.ConnectionStatus(ConnStatus.TIMEOUT);
            }
        };

        mConnTimer.start();

    }


    private void resetWifiReceivers(){
        Log.i(TAG, "resetWifiReceivers called: ");
        if ( mContext instanceof Activity){
            Log.i(TAG, "resetWifiReceivers called:1  ");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "stop and start receiving wifi config ");
                    stopReceivingWifiChanges();
                    startReceivingWifiChanges(mContext);
                }
            },2000);

        }

    }


    public enum ConnStatus {
        CONNECTION_START,
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        TIMEOUT,
        UNKNOWN
    }

    public interface ConnectionStatusInterface {
        void ConnectionStatus(ConnStatus status);

    }


}
