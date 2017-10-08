package com.gw.ecapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.text.format.Formatter;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created by Gobi on 08/10/17.
 */

public class AssociatedWifiHelper {


    public void getAssociatedWifi(Context context){

        new NetworkSniffTask(context).execute();

    }


    static class NetworkSniffTask extends AsyncTask<Void, Void, Void> {

        private static final String TAG = "nstask";

        private WeakReference<Context> mContextRef;

        public NetworkSniffTask(Context context) {
            mContextRef = new WeakReference<Context>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Let's sniff the network");

            try {
                Context context = mContextRef.get();

                if (context != null) {

                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                    WifiInfo connectionInfo = wm.getConnectionInfo();
                    int ipAddress = connectionInfo.getIpAddress();
                    String ipString = Formatter.formatIpAddress(ipAddress);


                    Log.d(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
                    Log.d(TAG, "ipString: " + String.valueOf(ipString));

                    String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                    Log.d(TAG, "prefix: " + prefix);

                    for (int i = 0; i < 255; i++) {
                        String testIp = prefix + String.valueOf(i);

                        InetAddress address = InetAddress.getByName(testIp);

                        boolean reachable = address.isReachable(1000);

                        String hostName = address.getCanonicalHostName();


                        if (reachable) {

                            NetworkInterface nwInterface = NetworkInterface.getByInetAddress(address);

                            if(null != nwInterface) {
                                byte[] mac = nwInterface.getHardwareAddress();

                                System.out.print("Current MAC address : ");

                                StringBuilder sb = new StringBuilder();
                                for (int j = 0; j < mac.length; j++) {
                                    sb.append(String.format("%02X%s", mac[j], (j < mac.length - 1) ? "-" : ""));
                                }
                                System.out.println(sb.toString());

                                Log.i(TAG, "Host: " + String.valueOf(hostName) + " is reachable!" + "Mac ID " + sb.toString() +
                                        " Display Name " + nwInterface.getDisplayName() + " Name is : " + nwInterface.getName());
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                Log.e(TAG, "Well that's not good.", t);
            }

            return null;
        }
    }


}
