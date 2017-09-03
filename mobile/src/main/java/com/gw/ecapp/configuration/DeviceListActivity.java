package com.gw.ecapp.configuration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by iningosu on 9/3/2017.
 */

public class DeviceListActivity extends AppCompatActivity {

    private ListView mDeviceListView;

    private  DeviceListAdapter mDeviceListAdapter;

    ArrayList<HashMap<String, String>> mWifiList;


    RelativeLayout mOverlayContainer;
    TextView mNoResultText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_layout);

        // Ui mapping here
        mOverlayContainer = (RelativeLayout) findViewById(R.id.overlay_container);
        mNoResultText = (TextView) findViewById(R.id.no_devices_found);

        // ui mapping done



        mDeviceListAdapter = new DeviceListAdapter(this);

        mDeviceListView = (ListView) findViewById(R.id.device_list);
        mDeviceListView.setAdapter(mDeviceListAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getWifiList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    private void getWifiList(){

        showOverlayWhileFetchingWifi();
        mWifiList =  NetworkUtils.getWifiAccessPointsList(false,DeviceListActivity.this);
        hideOverlayWhenWifiIsCompleted();

        mDeviceListAdapter.setData(mWifiList);
        mDeviceListAdapter.notifyDataSetChanged();
    }




    private void showOverlayWhileFetchingWifi(){
        mOverlayContainer.setVisibility(View.VISIBLE);
    }

    private void hideOverlayWhenWifiIsCompleted(){
        mOverlayContainer.setVisibility(View.GONE);

        mDeviceListView.setVisibility(mWifiList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoResultText.setVisibility(mWifiList.size() > 0 ? View.GONE : View.VISIBLE);


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
