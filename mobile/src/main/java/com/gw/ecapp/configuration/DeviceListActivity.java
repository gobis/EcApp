package com.gw.ecapp.configuration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gw.ecapp.AppConfig;
import com.gw.ecapp.AppUtils;
import com.gw.ecapp.DialogListener;
import com.gw.ecapp.DialogManager;
import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;
import com.gw.ecapp.WifiConnection;
import com.gw.ecapp.demo.DemoActivity;
import com.gw.ecapp.devicecontrol.DeviceControlListActivity;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.EngineUtils;
import com.gw.ecapp.engine.udpEngine.events.MessageArrivedEvent;
import com.gw.ecapp.engine.udpEngine.packetCreator.GetCpuMsgPacket;
import com.gw.ecapp.engine.udpEngine.parser.CpuInfoResponse;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;
import com.gw.ecapp.storage.DatabaseManager;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * Created by iningosu on 9/3/2017.
 */

public class DeviceListActivity extends Activity implements WifiConnection.ConnectionStatusInterface {

    private ListView mDeviceListView;

    private  DeviceListAdapter mDeviceListAdapter;

    ArrayList<HashMap<String, String>> mWifiList;


    RelativeLayout mOverlayContainer;
    TextView mNoResultText;

    private String mSelectedSSID;
    private String mSelectedDevicePassword;

    private Context mCurrentContext;

    private WifiConnection mWifiConnection;

    TextView mLoadingText;

    Button mScanButton;

    Handler mUiHandler;

    UDPClient mUdpClient ;

    private String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_layout);

        // Ui mapping here
        mOverlayContainer = (RelativeLayout) findViewById(R.id.overlay_container);
        mNoResultText = (TextView) findViewById(R.id.no_devices_found);

        mScanButton = (Button) findViewById(R.id.btn_scan);

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWifiList();
            }
        });

        mUiHandler = new Handler();

        mUdpClient  = UDPClient.getInstance(this);


        // ui mapping done

        mWifiConnection = WifiConnection.getInstance(this);

        mCurrentContext = DeviceListActivity.this;

        mDeviceListAdapter = new DeviceListAdapter(this);

        mDeviceListView = (ListView) findViewById(R.id.device_list);

        mLoadingText = (TextView) findViewById(R.id.loading_text);

        mDeviceListView.setAdapter(mDeviceListAdapter);

        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPasswordDialog(mWifiList.get(position).get(EngineUtils.SSID));
            }
        });

    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
        getWifiList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    private void getWifiList(){

        showOverlayWhileFetchingWifi();
        mWifiList =  NetworkUtils.getWifiAccessPointsList(
                NetworkUtils.NetworkFilter.FILTER_BY_DEVICE,DeviceListActivity.this);
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showPasswordDialog(final String selectedDevice) {
        mSelectedSSID = selectedDevice;
        DialogManager.showGenericConfirmDialogForOneButtons(
                DeviceListActivity.this,
                getString(R.string.connect_to_device_wifi, selectedDevice), getString(R.string.connect), new DialogListener() {
                    @Override
                    public void positiveButtonClick(String devicePassword) {
                        mSelectedDevicePassword = devicePassword;
                        Toast.makeText(DeviceListActivity.this, "password " + devicePassword, Toast.LENGTH_SHORT).show();
                        makeConnection(selectedDevice, devicePassword);
                    }
                });
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
        String currentSsid = NetworkUtils.getCurrentSsid(DeviceListActivity.this);

        // remove double quoute from leading and trail
        currentSsid = currentSsid.replaceAll("\"","");

        if(mSelectedSSID.equalsIgnoreCase(currentSsid)){
            Toast.makeText(mCurrentContext," Conneciton successful",Toast.LENGTH_SHORT).show();
            onSuccessfulConnection();
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
    public void makeConnection(final String deviceSsid, final String devicePassword){
        showLoadingWifiConnection();
        mWifiConnection.startReceivingWifiChanges(this);
        mWifiConnection.ConnectToServiceSetID(getApplicationContext(),deviceSsid,devicePassword,false);
    }


    private void connectionUnknownStatus(){
        onSuccessfulWifiConnection();
        Toast.makeText(mCurrentContext,getString(R.string.unable_connect_to_given_wifi,mSelectedSSID), Toast.LENGTH_SHORT).show();
    }

    public void showLoadingWifiConnection(){
        // get ssid and pwd , and make connection
        mOverlayContainer.setVisibility(View.VISIBLE);
        mLoadingText.setText(getString(R.string.connection_loading_text));
    }


    public void onSuccessfulWifiConnection(){
        // get ssid and pwd , and make connection
        mOverlayContainer.setVisibility(View.GONE);
    }

    private void showProgressWhileScanWifi(){
        mOverlayContainer.setVisibility(View.VISIBLE);
        mLoadingText.setText(getString(R.string.wait_for_scan_result));
    }


    private void hideProgressOnScanDone(){
        mOverlayContainer.setVisibility(View.GONE);
        mLoadingText.setText("");
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageArrivedEvent msgArriveEvent) {
        Log.i(TAG,"on Event called :: onEventMainThread info " + msgArriveEvent.message.toString());
        persistDeviceInfo((CpuInfoResponse) msgArriveEvent.message);
    }

    private void onSuccessfulConnection() {
        // get cpu info
        GetCpuMsgPacket cpuPacket = new GetCpuMsgPacket();
        UDPClient mEngine = (UDPClient) CommEngine.getCommsEngine(DeviceListActivity.this, CommEngine.ENGINE_TYPE.UDP);
        mEngine.sendMessageToDevice(AppUtils.getJsonFromObject(cpuPacket));
    }


    private void persistDeviceInfo(final CpuInfoResponse cpuInfo) {
        // write the information to data base

        final int channelCount = 4;


        List<DeviceModel> deviceModelList = new ArrayList<DeviceModel>();

        for (int i = 0; i < channelCount; i++) {

            DeviceModel deviceModel = new DeviceModel();

            deviceModel.setDeviceName(cpuInfo.getDeviceName());
            deviceModel.setChannelCount(channelCount);
            deviceModel.setConfigureName("User Testing Hall");
            deviceModel.setMacId(cpuInfo.getMacAddress());
            deviceModel.setApplianceName("Relay " + String.valueOf(i+1));
            deviceModel.setRelayNumber(String.valueOf(i+1));
            deviceModel.setDeviceSsid(mSelectedSSID);
            deviceModel.setDevicePassword(mSelectedDevicePassword);
            deviceModelList.add(deviceModel);
        }


        DatabaseManager dbManager = DatabaseManager.getInstance(DeviceListActivity.this);
        dbManager.bulkInsertOrUpdateDevice(deviceModelList)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        Log.i(TAG, " insertion/ update status " + aBoolean);
                        navigateToNextScreen();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i(TAG, "Exception in inserting models " + throwable.getMessage());
                    }
                });

    }


    private void insertAppliances(final Context context , List<ApplianceModel> applianceModels){

        DatabaseManager dbManager = DatabaseManager.getInstance(context);

        dbManager.bulkInsertOrUpdateAppliance(applianceModels)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        Log.i(TAG, " insertion/ update appliance status " + aBoolean);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i(TAG, "Exception in inserting models " + throwable.getMessage());
                    }
                });

    }


    private void navigateToNextScreen(){
        Log.i(TAG, "Navigating to next page");

        // move gateway to station mode
       /* StationModeHelper helper = new StationModeHelper();
        helper.setDeviceInStationMode(DeviceListActivity.this);*/

        // get cpu info , ip , mac address , wifiName etc
        Intent intent = new Intent(this, DeviceControlListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
