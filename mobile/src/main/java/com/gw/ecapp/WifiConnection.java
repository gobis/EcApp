package com.gw.ecapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.util.Log;

import com.gw.ecapp.engine.udpEngine.EngineUtils;

import java.util.List;

/**
 * Created by iningosu on 9/3/2017.
 */

public class WifiConnection {


    public static WifiConnection INSTANCE;

    private boolean mIsConnInProgress;

    Context mContext;

    ConnectionStatusInterface mWifiConnCallBack;

    CountDownTimer mConnTimer;

    String TAG = getClass().getSimpleName();


    public static synchronized WifiConnection getInstance(ConnectionStatusInterface callback){

        if(INSTANCE ==  null){
            INSTANCE = new WifiConnection();
        }
        INSTANCE.mWifiConnCallBack =  callback;
        return  INSTANCE;
    }


     private WifiConnection(){

     }



     public boolean isConnectionInProgress(){
         return mIsConnInProgress ;
     }


    public void ConnectToServiceSetID(Context context, String serviceSetID, String pwd) {
        try {

            removeNetworkIfMatches(context,serviceSetID);

            startConnectionTimer();

            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            Log.i(getClass().getSimpleName(), " Creating Connection with " + serviceSetID + " passkey " + pwd);
            WifiConfiguration wc = new WifiConfiguration();
            wc.SSID = "\"" + serviceSetID + "\"";
            wc.preSharedKey = "\"" + pwd + "\"";
            wc.hiddenSSID = true;
            wc.status = WifiConfiguration.Status.ENABLED;

//           int mNetID = NetworkCheck.checkPreviousConfiguration(this, wc);

            int mNetID = wifi.addNetwork(wc);

            if (mNetID == -1) {
                mNetID = wifi.addNetwork(wc);
            }

            if (mNetID != -1) {
                wifi.disconnect();
                wifi.enableNetwork(mNetID, true);
                // wifi.saveConfiguration();
                wifi.reconnect();

                Log.i(getClass().getSimpleName(), "Will establish connection soon with " + serviceSetID);

            } else {
                Log.i(getClass().getSimpleName(), " Unable to create connection with " + serviceSetID);
            }
        } catch (Exception e) {
            Log.i(getClass().getSimpleName(), "Wifi connection  exception " + e.toString());
            handleExceptionWhileWifiConnection();
        }

    }


    private void handleExceptionWhileWifiConnection(){
        stopReceivingWifiChanges();
        mWifiConnCallBack.ConnectionStatus(ConnStatus.UNKNOWN);
    }


    public void startReceivingWifiChanges(Context context){
        mContext = context ;
        if(null != mContext) {
            mContext.registerReceiver(this.myWifiReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }


    public  void stopReceivingWifiChanges(){
        if(null != mContext) {
            mContext.unregisterReceiver(myWifiReceiver);
        }
    }



    private final BroadcastReceiver myWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            // if active network type is wifi
            if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                if (activeNetwork.getState() == NetworkInfo.State.CONNECTING) {
                    mWifiConnCallBack.ConnectionStatus(ConnStatus.CONNECTING);
                }else if (activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
                    mConnTimer.cancel();
                    mWifiConnCallBack.ConnectionStatus(ConnStatus.CONNECTED);
                }else if (activeNetwork.getState() == NetworkInfo.State.DISCONNECTING) {

                } else if (activeNetwork.getState() == NetworkInfo.State.DISCONNECTED) {
                    mWifiConnCallBack.ConnectionStatus(ConnStatus.DISCONNECTED);

                }else if (activeNetwork.getState() == NetworkInfo.State.UNKNOWN) {

                }

            } else {


            }


        }
    };


    /**
     * remove the current network from list if matches
     * @param selectedSSID
     */
    private void removeNetworkIfMatches(Context context, String selectedSSID){

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        String connectedSsid = NetworkUtils.getCurrentSsid(context);

        if(null != connectedSsid) {
            for (WifiConfiguration i : list) {
                if (connectedSsid.equalsIgnoreCase(i.SSID)) {
                    Log.i(TAG, "removeNetworkIfMatches removing network from the list ");
                    wifiManager.removeNetwork(i.networkId);
                    wifiManager.saveConfiguration();
                }
            }
        }

    }

    /**
     * start the timer before making connection
     */
    private void startConnectionTimer(){

        mConnTimer = new CountDownTimer(EngineUtils.WIFI_CONN_WAIT_TIME,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                stopReceivingWifiChanges();
                mWifiConnCallBack.ConnectionStatus(ConnStatus.TIMEOUT);
            }
        };

        mConnTimer.start();

    }


    public enum ConnStatus{
        CONNECTION_START,
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        TIMEOUT,
        UNKNOWN
    }

    public interface ConnectionStatusInterface{
        void ConnectionStatus(ConnStatus status);

    }




}
