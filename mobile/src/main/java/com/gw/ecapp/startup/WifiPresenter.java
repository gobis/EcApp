package com.gw.ecapp.startup;

import android.content.Context;

import com.gw.ecapp.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by iningosu on 8/27/2017.
 */

public class WifiPresenter {

    private Context mContext;

    public WifiPresenter(Context context){
        mContext = context ;
    }


    public ArrayList<HashMap<String, String>> getWifiAccessPointsList(){
        return  NetworkUtils.getWifiAccessPointsList(false,mContext);
    }


}
