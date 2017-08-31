package com.gw.ecapp.startup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gw.ecapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by iningosu on 8/27/2017.
 */

public class WifiActivity extends AppCompatActivity {


    RelativeLayout mMasterContainer;
    RelativeLayout mLoadingContainer;

    TextView mLoadingText;


    Button mWifiConnect;
    Button mSkipWifiConnect;
    Button mScanAgain;

    WifiPresenter mWifiPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_activity_layout);

        mWifiPresenter = new WifiPresenter(WifiActivity.this);

        mMasterContainer = (RelativeLayout) findViewById(R.id.master_container);
        mLoadingContainer = (RelativeLayout) findViewById(R.id.loading_container);

        mLoadingText = (TextView) findViewById(R.id.loading_text);

        mWifiConnect = (Button) findViewById(R.id.btn_connect);
        mSkipWifiConnect = (Button) findViewById(R.id.btn_skip);
        mScanAgain = (Button) findViewById(R.id.btn_scan);

    }

    @Override
    protected void onStart() {
        super.onStart();

        scanForWifi();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void onScanClick(View v){
        // scan for wifi
        scanForWifi();
    }


    public void onSkipClick(View v){
     // move it to next page

    }


    public void onConnectClick(View v){
        showLoadingWifiConnection();

    }


    private void scanForWifi(){
        showProgressWhileScanWifi();
        ArrayList<HashMap<String, String>> wifiData = mWifiPresenter.getWifiAccessPointsList();



    }



    public void showLoadingWifiConnection(){
        // get ssid and pwd , and make connection
        mMasterContainer.setVisibility(View.GONE);
        mLoadingContainer.setVisibility(View.VISIBLE);
        mLoadingText.setText(getString(R.string.connection_loading_text));
    }


    public void onSuccessfulWifiConnection(){
        // get ssid and pwd , and make connection
        mLoadingContainer.setVisibility(View.GONE);
        mMasterContainer.setVisibility(View.VISIBLE);

    }

    private void showProgressWhileScanWifi(){
        mMasterContainer.setVisibility(View.GONE);
        mLoadingContainer.setVisibility(View.VISIBLE);
        mLoadingText.setText(getString(R.string.wait_for_scan_result));
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
