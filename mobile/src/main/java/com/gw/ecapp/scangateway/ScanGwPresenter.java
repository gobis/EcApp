package com.gw.ecapp.scangateway;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.gw.ecapp.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by iningosu on 8/27/2017.
 */

public class ScanGwPresenter {

    private Context mContext;

    public ScanGwPresenter(Context context){
        mContext = context ;
    }


    public ArrayList<HashMap<String, String>> getWifiAccessPointsList(){
        return  NetworkUtils.getWifiAccessPointsList(false,mContext);
    }


}
