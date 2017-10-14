package com.gw.ecapp.devicecontrol;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iningosu on 9/10/2017.
 *
 * This is landing page when all the devices are configured
 *
 *
 */

public class DeviceControlListActivity extends Activity {

    private RecyclerView mDeviceRecyclerView;
    private DeviceControlListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_list);

        mDeviceRecyclerView = (RecyclerView)findViewById(R.id.device_recycle_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mDeviceRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new DeviceControlListAdapter(this);
        mDeviceRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onDataLoaded();
    }


    private void onDataLoaded(){
        // testing
        List<DeviceModel> deviceList = new ArrayList<>();

        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setChannelCount(1);
        deviceModel.setDeviceName("BedRoom 1");

        ApplianceModel applianceModel = new ApplianceModel();
        applianceModel.setDeviceName("Fan");
        ArrayList<ApplianceModel> list = new ArrayList<>();
        list.add(applianceModel);

        deviceModel.setConnectedDevices(list);


        DeviceModel deviceModel2 = new DeviceModel();
        deviceModel2.setChannelCount(4);
        deviceModel2.setDeviceName("Hall");

        ApplianceModel applianceModel1 = new ApplianceModel();
        applianceModel1.setDeviceName("Fan");

        ApplianceModel applianceModel2 = new ApplianceModel();
        applianceModel2.setDeviceName("TV");

        ApplianceModel applianceModel3 = new ApplianceModel();
        applianceModel3.setDeviceName("Geezer");

        ApplianceModel applianceModel4 = new ApplianceModel();
        applianceModel4.setDeviceName("AC");

        ArrayList<ApplianceModel> list2 = new ArrayList<>();
        list2.add(applianceModel);
        list2.add(applianceModel2);
        list2.add(applianceModel3);
        list2.add(applianceModel4);

        deviceModel2.setConnectedDevices(list2);


        deviceList.add(deviceModel);
        deviceList.add(deviceModel2);


        mAdapter.setData(deviceList);
        mAdapter.notifyDataSetChanged();
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



    public void onAddNewDevice(View v){
        Toast.makeText(DeviceControlListActivity.this," Add new Device",Toast.LENGTH_SHORT).show();
    }


}
