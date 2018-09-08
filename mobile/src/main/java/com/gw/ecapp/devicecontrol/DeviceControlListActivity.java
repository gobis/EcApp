package com.gw.ecapp.devicecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.util.ArraySet;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.AppUtils;
import com.gw.ecapp.DialogManager;
import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;
import com.gw.ecapp.TwoButtonDialogListener;
import com.gw.ecapp.WifiConnection;
import com.gw.ecapp.configuration.DeviceListActivity;
import com.gw.ecapp.devicecontrol.edit.DeviceEditActivity;
import com.gw.ecapp.devicecontrol.events.ApplianceControlEvent;
import com.gw.ecapp.devicecontrol.events.DeviceEditEvent;
import com.gw.ecapp.devicecontrol.stationmode.StationModeHelper;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.EngineUtils;
import com.gw.ecapp.engine.udpEngine.events.MessageArrivedEvent;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPRequestStatus;
import com.gw.ecapp.sniffnetwork.AssociatedWifiHelper;
import com.gw.ecapp.storage.AppPreferences;
import com.gw.ecapp.storage.DatabaseManager;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iningosu on 9/10/2017.
 * <p>
 * This is landing page when all the devices are configured
 */

public class DeviceControlListActivity extends AppCompatActivity
        implements WifiConnection.ConnectionStatusInterface ,
        AssociatedWifiHelper.NetworkSniffStatus , UDPRequestStatus {

    private RecyclerView mDeviceRecyclerView;
    private DeviceControlListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private WifiConnection mWifiConnection;
    private Context mCurrentContext;

    private TextView mLoadingText;
    private RelativeLayout mOverlayContainer;
    private TextView mNoResultText;

    private String mSelectedSSID ;

    private ApplianceControlEvent mControlEvent;

    private DeviceControlListPresenter mControlListPresenter;

    private EditText mSendMessageText;
    private Button mSendButton;
    private LinearLayout mSendMsgLayout;

    private int EDIT_ACTIVITY_RESULT  = 765;
    private ActionBar mAppBar;

    private RelativeLayout mMasterDeviceListLayout;
    private RelativeLayout mMasterConsoleLayout;

    List<String> deviceMacList ;



    private String TAG = getClass().getSimpleName();

    private AppUtils.ConnMode mCurrentConnMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_list);

        mLoadingText = (TextView) findViewById(R.id.loading_text);
        // Ui mapping here
        mOverlayContainer = (RelativeLayout) findViewById(R.id.overlay_container);
        mNoResultText = (TextView) findViewById(R.id.no_devices_found);

        mDeviceRecyclerView = (RecyclerView) findViewById(R.id.device_recycle_view);

        mSendButton = (Button) findViewById(R.id.send_message_btn);
        mSendMessageText = (EditText) findViewById(R.id.send_message_text);
        mSendMsgLayout = (LinearLayout) findViewById(R.id.send_message_layout);

        mMasterDeviceListLayout = (RelativeLayout) findViewById(R.id.device_list_layout);
        mMasterConsoleLayout = (RelativeLayout) findViewById(R.id.console_layout);

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

        mAppBar = getSupportActionBar();
        mAppBar.setTitle(getString(R.string.devices));

        mCurrentConnMode =  getCurrentConnectionMode();

        mControlListPresenter = new DeviceControlListPresenter();

        Intent intent = getIntent();
        boolean scanLocalNetwork =  intent.getBooleanExtra(AppConstant.Extras.SCAN_LAN,false);

        if(scanLocalNetwork){
            // already in station mode , start sniff the network
            startScanLocalNetwork();
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.device_control_menu, menu);
        return true;
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_station_mode:
                //  show popup with wifi-router info , where user going to connect
                // if yes, connect all the devices with router info
                // else, show the first page
                if(AppPreferences.getInstance(DeviceControlListActivity.this).hasRouter()){
                    showStationModeDialog();
                }else{
                    showWifiDialog();
                }

                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;

            case R.id.action_help:
                Toast.makeText(this, "Help selected", Toast.LENGTH_SHORT)
                        .show();
                break;

            case R.id.action_scan_device:
                startScanLocalNetwork();
                break;
            case R.id.action_clear_log:
                break;
            case R.id.action_enable_log:
                break;
            case R.id.action_send_text:
                enableTextCommand();
                break;
            default:
                break;
        }

        return true;
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
    public void onBackPressed() {
        DeviceControlListActivity.this.moveTaskToBack(true);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DeviceEditEvent editEvent) {
        Log.i(TAG, "on Event called :: onEventMainThread info " + editEvent.getDeviceModel().toString());
        updateDeviceConfig(editEvent.getDeviceModel());
    }

    public void onAddNewDevice(View v) {
        Toast.makeText(DeviceControlListActivity.this, " Add new Device", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DeviceControlListActivity.this, DeviceListActivity.class);
        startActivity(intent);
    }

    /**
     * populate data from database
     */
    private void populateDataFromDb() {

        DatabaseManager dbManager = DatabaseManager.getInstance(DeviceControlListActivity.this);
        dbManager.getDeviceList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<DeviceModel>>() {
                    @Override
                    public void accept(List<DeviceModel> deviceModels) throws Exception {

                        List<DeviceModel> deviceModelList = new ArrayList<DeviceModel>();

                        for (DeviceModel deviceModel : deviceModels) {
                            Log.i(TAG, deviceModel.getMacId() + "  " + deviceModel.toString());

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
                    public void accept( Throwable throwable) throws Exception {
                        Log.i(TAG, "Exception in inserting models " + throwable.getMessage());
                    }
                });
    }


    private void executeOperation(ApplianceControlEvent controlEvent) {
        mControlEvent = controlEvent ;

        if (AppPreferences.getInstance(DeviceControlListActivity.this).hasRouter()) {
            // System is already configured in station mode
            sendCommand(controlEvent);
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

    private void enableTextCommand(){
        // show the layout to send the text
        mSendMsgLayout.setVisibility(View.VISIBLE);
    }

    private void clearLogs(){

    }

    private void enableLogs(){

    }

    private void disableLogs(){

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
        if(mOverlayContainer.getVisibility() != View.VISIBLE) {
            mOverlayContainer.setVisibility(View.VISIBLE);
        }
    }


    private void sendCommand(final ApplianceControlEvent controlEvent){
        // get cpu info
        UDPClient mEngine = (UDPClient) CommEngine.getCommsEngine(DeviceControlListActivity.this, CommEngine.ENGINE_TYPE.UDP,this);
        mEngine.sendMessageToDevice(controlEvent.mMessage);
    }


    private void updateDeviceConfig(DeviceModel deviceModel){
        Intent intent = new Intent(DeviceControlListActivity.this, DeviceEditActivity.class);
        intent.putExtra(AppConstant.Extras.Device, Parcels.wrap(deviceModel));
        startActivityForResult(intent,EDIT_ACTIVITY_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_ACTIVITY_RESULT) {
                Log.i(TAG, " Config Saved ");
            }
        }

    }

    /**
     * user has provided router name and password already
     * now, he wants to connect all devices using this credentials
     * display credentials before connecting it
     * this is not only for cross verification, also serves the purpose of password change
     */
    private void showStationModeDialog(){

        final String ssid = AppPreferences.getInstance(DeviceControlListActivity.this).getRouterSSID();
        final String password = AppPreferences.getInstance(DeviceControlListActivity.this).getRouterPassword();

        DialogManager.showGenericConfirmDialogForTwoButtons(
                DeviceControlListActivity.this,
                getString(R.string.station_mode_confirm_msg , ssid , password),
                getString(R.string.yes), getString(R.string.change_password), new TwoButtonDialogListener() {

                    @Override
                    public void positiveButtonClicked() {
                        // start setting station mode

                        // connect to device one by one and send the station mode command
                        Toast.makeText(DeviceControlListActivity.this,
                                "Moving to station mode", Toast.LENGTH_SHORT).show();
                        // TODO: pass command to station mode
                        StationModeHelper stationModeHelper = new StationModeHelper();
                        stationModeHelper.setDeviceInStationMode(DeviceControlListActivity.this);

                        stationModeHelper.restartDevice(DeviceControlListActivity.this);

                        // TODO: make some 3 sec delay
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 connectToRouter();
                             }
                         });
                    }

                    @Override
                    public void negativeButtonClicked() {
                        // user password has changed , take user to wifi page
                    }
                });

    }

    /**
     * show this dialog if user doesn't have wifi router and want to connect with new router
     *
     */
    private void showWifiDialog(){

        DialogManager.showGenericConfirmDialogForTwoButtons(
                DeviceControlListActivity.this,
                getString(R.string.no_router_info), getString(R.string.yes), getString(R.string.no), new TwoButtonDialogListener() {

                    @Override
                    public void positiveButtonClicked() {
                        // take user to first page

                    }

                    @Override
                    public void negativeButtonClicked() {
                        // user don't want to provide info , continue as AP mode

                    }
                });

    }

    /**
     * this function will be called once
     *  1. set station mode command
     *  2. call restart command
     *  pref file has router username and password
     *
     */
    @UiThread
    private void connectToRouter(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final String ssid = AppPreferences.getInstance(DeviceControlListActivity.this).getRouterSSID();
                final String password = AppPreferences.getInstance(DeviceControlListActivity.this).getRouterPassword();
                makeConnection(ssid,password);
            }
        }, 3000);
    }

    private void startScanLocalNetwork(){
        // this needs to be triggered when phone connected to WIFI router
        // how to ensure phone connected to WIFI router
        // get the current connection and check with WIFI data stored , both are matching then you can start sniff
        final String ssid = AppPreferences.getInstance(DeviceControlListActivity.this).getRouterSSID();

        String currentSsid = NetworkUtils.getCurrentSsid(DeviceControlListActivity.this);
        // remove double quoute from leading and trail
        currentSsid = currentSsid.replaceAll("\"","");
        if(currentSsid.equalsIgnoreCase(ssid)) {
            macAddressList();
        }else{
            Toast.makeText(mCurrentContext, "You are connected to different router, make connection with " + ssid, Toast.LENGTH_SHORT).show();
        }
    }

    // this function will tell you in
    private AppUtils.ConnMode getCurrentConnectionMode() {
        AppUtils.ConnMode connMode = AppUtils.ConnMode.AP_MODE;
        List<DeviceModel> deviceModelList = mAdapter.getDeviceList();
        return connMode;
    }


    private List<String> macAddressList(){
        List<String> macAddressList = new ArrayList<>();
        try {

            DatabaseManager dbManager = DatabaseManager.getInstance(DeviceControlListActivity.this);
            dbManager.getDeviceList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<DeviceModel>>() {
                        @Override
                        public void accept(List<DeviceModel> deviceModels) throws Exception {
                            Set<String> macSet = new ArraySet<String>();
                            Set<String> ipSet = new ArraySet<String>();
                            for (DeviceModel deviceModel : deviceModels) {
                                macSet.add(deviceModel.getMacId());

                                String ip = deviceModel.getLastConnectedIP();
                                if(ip != null && ip.trim().length()>0){
                                    ipSet.add(ip);
                                }

                                ip = deviceModel.getPreferredIP();
                                if(ip != null && ip.trim().length()>0){
                                    ipSet.add(ip);
                                }
                            }

                            List<String> macList = new ArrayList<String>(macSet);
                            deviceMacList = macList;

                            List<String> ipList = new ArrayList<String>(ipSet);
                            scanNetwork(macList, ipList);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {


                        }
                    });
        }catch (Exception ex){
            Log.e(TAG,"Exception in getting devices");
        }

        return macAddressList;
    }

    private void scanNetwork(List<String> macList , List<String> smartIpList) {
        AssociatedWifiHelper helper = new AssociatedWifiHelper(
                DeviceControlListActivity.this,macList,smartIpList);
        helper.startSniffingNetwork();
    }

    @Override
    public void sniffStarted(String ipMask) {
        // show loading UI
        Log.i(TAG," Network sniff started, Mask IP => " + ipMask);
        showOverlay();
        mLoadingText.setText(R.string.find_device_ip);
    }

    @Override
    public void sniffCompleted(final ConcurrentHashMap<String,String> macMap) {
        // hide loading UI
        // Log.i(TAG," Network sniff completed");
        // map key is IP value is mac address

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.i(TAG," Network sniff completed, values are  => " + macMap.toString());

                boolean isDeviceFound = false ;

                updatePreferredIPList(macMap);

                for (String mac : deviceMacList) {
                    String ip = macMap.get(mac.toLowerCase());
                    if(ip != null){
                        isDeviceFound = true ;
                        Toast.makeText(DeviceControlListActivity.this,"IP => " + ip,Toast.LENGTH_LONG).show();
                        Log.i(TAG," Device is connected to " + ip);
                        // currently there is only one device, hence set the IP and break it
                        EngineUtils.setUdpUniCastIp(ip, EngineUtils.UDP_UNI_CAST_PORT);
                        break;
                    }
                }

                // hide loading UI
                hideOverLay();

                if(!isDeviceFound){
                    showExitAppDialog();
                }
            }
        });
    }

    private void updatePreferredIPList(final ConcurrentHashMap<String,String> macMap){

      // get all device and check for mac id and update hte preferred Ip and last IP connected
        try {

            DatabaseManager dbManager = DatabaseManager.getInstance(DeviceControlListActivity.this);
            dbManager.getDeviceList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<DeviceModel>>() {
                        @Override
                        public void accept(List<DeviceModel> deviceModels) throws Exception {
                            updateDevices(macMap,deviceModels);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {


                        }
                    });
        }catch (Exception ex){
            Log.e(TAG,"Exception in getting devices");
        }

    }

    private void updateDevices(ConcurrentHashMap<String,String> map, List<DeviceModel> deviceModelList){
        Log.i(TAG,"updateDevices start");
        boolean isUpdatedNeeded = false;
        for (DeviceModel deviceModel : deviceModelList) {
            String macId = deviceModel.getMacId();

            String ip = map.get(macId);

            if(ip  != null){
                String lastConnectedIP = deviceModel.getLastConnectedIP();
                if(!ip.equalsIgnoreCase(lastConnectedIP)) {
                    deviceModel.setLastConnectedIP(ip);
                    isUpdatedNeeded = true;
                }
                // pref connected IP needs to be tuned
                String preferredIP = deviceModel.getPreferredIP();
                if(!ip.equalsIgnoreCase(preferredIP)) {
                    deviceModel.setPreferredIP(ip);
                    isUpdatedNeeded = true;
                }
            }
        }

        if(isUpdatedNeeded){
            Log.i(TAG,"updation of device is needed");
            persistUpdatedInfo(deviceModelList);
        }
    }

    private void persistUpdatedInfo(List<DeviceModel> deviceModelList) {

        DatabaseManager dbManager = DatabaseManager.getInstance(DeviceControlListActivity.this);
        dbManager.bulkUpdateDevice(deviceModelList)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        Toast.makeText(DeviceControlListActivity.this,"IP updated successfully",Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                    }
                });
    }


    private void showExitAppDialog(){
        DialogManager.showGenericConfirmDialogForTwoButtons(
                DeviceControlListActivity.this,
                getString(R.string.unable_to_find_device), getString(R.string.try_again), getString(R.string.exit_app), new TwoButtonDialogListener() {

                    @Override
                    public void positiveButtonClicked() {
                       // try scanning again
                       startScanLocalNetwork();
                    }

                    @Override
                    public void negativeButtonClicked() {
                       // exit app
                       finishAndRemoveTask();
                    }
                });
    }

    /**
     * call back when udp request is successful
     */
    @Override
    public void requestSuccess() {

    }

    /**
     * call back when given requst if timeout
     */
    @Override
    public void requestTimeOut() {

    }

    /**
     * call back for retry count
     *
     * @param retryCount
     */
    @Override
    public void requestRetryCount(int retryCount) {

    }
}
