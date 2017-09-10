package com.gw.ecapp.scangateway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;

public class ScanActivity extends AppCompatActivity {

    ScanGwPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mPresenter = new ScanGwPresenter(ScanActivity.this);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.getWifiAccessPointsList();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    // start gateway scanning

    private void startScanForGateway(){
        // start the progress bar
        NetworkUtils.getWifiAccessPointsList(true,ScanActivity.this);

    }


    /**
     *
     */
    private void startProgressbar(){

    }

    /**
     *
     */
    private void showGatewayList(){

    }

    /**
     *
     */
    private void showErrorPage(){

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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}
