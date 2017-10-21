package com.gw.ecapp.devicecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.AppUtils;
import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;
import com.gw.ecapp.WifiConnection;
import com.gw.ecapp.configuration.DeviceListActivity;
import com.gw.ecapp.devicecontrol.events.ApplianceControlEvent;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.events.MessageArrivedEvent;
import com.gw.ecapp.engine.udpEngine.parser.CpuInfoResponse;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;
import com.gw.ecapp.storage.AppPreferences;
import com.gw.ecapp.storage.DatabaseManager;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iningosu on 9/10/2017.
 * <p>
 * This is landing page when all the devices are configured
 */

public class DeviceControlListActivity extends Activity  implements WifiConnection.ConnectionStatusInterface {

    private RecyclerView mDeviceRecyclerView;
    private DeviceControlListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private WifiConnection mWifiConnection;
    private Context mCurrentContext;

    TextView mLoadingText;
    RelativeLayout mOverlayContainer;
    TextView mNoResultText;

    private String mSelectedSSID ;

    private ApplianceControlEvent mControlEvent;


    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_list);

        mLoadingText = (TextView) findViewById(R.id.loading_text);
        // Ui mapping here
        mOverlayContainer = (RelativeLayout) findViewById(R.id.overlay_container);
        mNoResultText = (TextView) findViewById(R.id.no_devices_found);

        mDeviceRecyclerView = (RecyclerView) findViewById(R.id.device_recycle_view);

        hideOverLay();

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mDeviceRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new DeviceControlListAdapter(this);
        mDeviceRecyclerView.setAdapter(mAdapter);

        mWifiConnection = WifiConnection.getInstance(this);
        mCurrentContext = DeviceControlListActivity.this;


        AppPreferences.getInstance(DeviceControlListActivity.this).setConfigStatus(true);


        // DeviceModel deviceModel = Parcels.unwrap(getIntent().getParcelableExtra(AppConstant.Extras.Device));

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        // call database and populate
        populateDataFromDb();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ApplianceControlEvent controlEvent) {
        executeOperation(controlEvent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageArrivedEvent msgArriveEvent) {
        Log.i(TAG, "on Event called :: onEventMainThread info " + msgArriveEvent.message.toString());
    }


    public void onAddNewDevice(View v) {
        Toast.makeText(DeviceControlListActivity.this, " Add new Device", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DeviceControlListActivity.this, DeviceListActivity.class);
        startActivity(intent);
    }


    private void populateDataFromDb() {


        DatabaseManager dbManager = DatabaseManager.getInstance(DeviceControlListActivity.this);
        dbManager.getDeviceList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<DeviceModel>>() {
                    @Override
                    public void accept(@NonNull List<DeviceModel> deviceModels) throws Exception {

                        List<DeviceModel> deviceModelList = new ArrayList<DeviceModel>();

                        for (DeviceModel deviceModel : deviceModels) {
                            Log.i(TAG, deviceModel.getMacId() + "    " + deviceModel.toString());

                            if (deviceModelList.size() == 0) {
                                deviceModelList.add(deviceModel);
                            } else {
                                int deviceCount = deviceModelList.size();
                                boolean addDevice = true;
                                for (int i = 0; i < deviceCount; i++) {
                                    DeviceModel dModel = deviceModelList.get(i);
                                    if (addDevice && dModel.getMacId().equals(deviceModel.getMacId())) {
                                        addDevice = false;
                                    }
                                }
                                if (addDevice) {
                                    deviceModelList.add(deviceModel);
                                }
                            }
                        }

                        for (DeviceModel dModel : deviceModelList) {

                            ArrayList<ApplianceModel> connectedDevices = new ArrayList<ApplianceModel>();

                            for (DeviceModel deviceModel : deviceModels) {
                                if (dModel.getMacId().equals(deviceModel.getMacId())) {
                                    ApplianceModel appModel = new ApplianceModel();
                                    appModel.setRelayNumber(deviceModel.getRelayNumber());
                                    appModel.setDeviceName(deviceModel.getApplianceName());
                                    connectedDevices.add(appModel);
                                }
                            }
                            dModel.setConnectedDevices(connectedDevices);
                        }

                        mAdapter.setData(deviceModelList);
                        mAdapter.notifyDataSetChanged();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i(TAG, "Exception in inserting models " + throwable.getMessage());
                    }
                });
    }


    private void executeOperation(ApplianceControlEvent controlEvent) {
        mControlEvent = controlEvent ;

        if (AppPreferences.getInstance(DeviceControlListActivity.this).hasRouter()) {
            // System is already configured in station mode


        } else {

            // System is in AP mode only..
            // check currently where you are connecting to .. if both are same, then ignore it
            DeviceModel deviceModel = controlEvent.getDeviceModel();
            String controlSsid = deviceModel.getDeviceSsid();
            String controlPassword = deviceModel.getDevicePassword();

             String currentSsid = NetworkUtils.getCurrentSsid(DeviceControlListActivity.this);
            // remove double quoute from leading and trail
             currentSsid = currentSsid.replaceAll("\"","");
            if(currentSsid.equalsIgnoreCase(controlSsid)){
                sendCommand(controlEvent);

            }else{
                // connect to different network and start sending commands
                makeConnection(controlSsid,controlPassword);

            }
        }
    }


    @Override
    public void ConnectionStatus(WifiConnection.ConnStatus status) {

        switch (status){
            case CONNECTING:
                break;
            case CONNECTED:
                checkWifiConnection();
                break;
            case DISCONNECTED:
                break;
            case CONNECTION_START:
                break;
            case TIMEOUT:
                connectionTimeOut();
                break;
            case UNKNOWN:
                connectionUnknownStatus();
                break;
        }
    }


    private void checkWifiConnection(){

        onSuccessfulWifiConnection();
        // get currently connected ssid
        String currentSsid = NetworkUtils.getCurrentSsid(mCurrentContext);

        // remove double quoute from leading and trail
        currentSsid = currentSsid.replaceAll("\"","");

        if(mSelectedSSID.equalsIgnoreCase(currentSsid)){
            Toast.makeText(mCurrentContext," Connection successful",Toast.LENGTH_SHORT).show();
            onSuccessfulWifiConnection();
        }else {
            Toast.makeText(mCurrentContext,getString(R.string.connect_to_wrong_ssid),Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * responsible to handle connection time out when user unable to connect to network
     */
    private void connectionTimeOut(){
        onSuccessfulWifiConnection();
        Toast.makeText(mCurrentContext,getString(R.string.unable_connect_to_given_wifi,mSelectedSSID), Toast.LENGTH_SHORT).show();

    }

    /**
     * responsible to handle connection time out when user unable to connect to network
     */
    public void makeConnection(String deviceSsid, String devicePassword){
        mSelectedSSID = deviceSsid;
        showLoadingWifiConnection();
        mWifiConnection.startReceivingWifiChanges(getApplicationContext());
        mWifiConnection.ConnectToServiceSetID(getApplicationContext(),deviceSsid,devicePassword,false);
    }


    private void connectionUnknownStatus(){
        hideOverLay();
        Toast.makeText(mCurrentContext,getString(R.string.unable_connect_to_given_wifi,mSelectedSSID), Toast.LENGTH_SHORT).show();
    }

    public void showLoadingWifiConnection(){
        // get ssid and pwd , and make connection
        showOverlay();
        mLoadingText.setText(getString(R.string.connection_loading_text));
    }


    public void onSuccessfulWifiConnection(){
        // get ssid and pwd , and make connection
        hideOverLay();
        executeOperation(mControlEvent);
    }

    private void hideOverLay(){
        mOverlayContainer.setVisibility(View.GONE);
    }

    private  void showOverlay(){
        mOverlayContainer.setVisibility(View.VISIBLE);
    }


    private void sendCommand(final ApplianceControlEvent controlEvent){
        // get cpu info
        UDPClient mEngine = (UDPClient) CommEngine.getCommsEngine(DeviceControlListActivity.this, CommEngine.ENGINE_TYPE.UDP);
        mEngine.sendMessageToDevice(controlEvent.mMessage);
    }

}
