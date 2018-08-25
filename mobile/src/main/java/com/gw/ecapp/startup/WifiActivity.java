package com.gw.ecapp.startup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.NsdHelper;
import com.gw.ecapp.sniffnetwork.AssociatedWifiHelper;
import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;
import com.gw.ecapp.WifiConnection;
import com.gw.ecapp.configuration.DeviceListActivity;
import com.gw.ecapp.engine.udpEngine.EngineUtils;
import com.gw.ecapp.startup.SpinnerAdapter.WifiSsidAdapter;
import com.gw.ecapp.storage.AppPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by iningosu on 8/27/2017.
 *  on start of activity, it will scan the wifi
 *  user can make connection or skip / and add it later
 *
 */

public class WifiActivity extends AppCompatActivity
        implements WifiConnection.ConnectionStatusInterface , AssociatedWifiHelper.NetworkSniffStatus {


    RelativeLayout mMasterContainer;
    RelativeLayout mOverlayContainer;

    TextView mLoadingText;

    Spinner mWifiListSpinner;
    EditText mWifiPwd;

    Button mWifiConnect;
    Button mSkipWifiConnect;
    Button mScanAgain;

    WifiPresenter mWifiPresenter;
    WifiSsidAdapter mWifiSsidAdapter;

    ArrayList<HashMap<String, String>> mWifiListData;

    private String mSelectedSSID;
    private String mPassword;

    private WifiConnection mWifiConnection;

    private Context mCurrentContext;

    boolean mFromEditPage = false ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_activity_layout);

        mWifiPresenter = new WifiPresenter(WifiActivity.this);

        mCurrentContext = WifiActivity.this;


        // Ui Mapping start
        mMasterContainer = (RelativeLayout) findViewById(R.id.master_container);
        mOverlayContainer = (RelativeLayout) findViewById(R.id.overlay_container);

        mLoadingText = (TextView) findViewById(R.id.loading_text);

        mWifiListSpinner = (Spinner) findViewById(R.id.wifi_ssid_spinner);
        mWifiPwd = (EditText) findViewById(R.id.wifi_pwd);

        mWifiConnect = (Button) findViewById(R.id.btn_connect);
        mSkipWifiConnect = (Button) findViewById(R.id.btn_skip);
        mScanAgain = (Button) findViewById(R.id.btn_scan);

        // Ui Mapping ends here

        mWifiConnection = WifiConnection.getInstance(this);

        mFromEditPage = getIntent().getBooleanExtra(AppConstant.Extras.FROM_DEVICE_EDIT_PAGE,false);

       // FROM_DEVICE_EDIT_PAGE
        if(mFromEditPage){
             // hide skip button
            mSkipWifiConnect.setVisibility(View.GONE);

        }


        mWifiListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> wifiData = mWifiListData.get(position);
                mSelectedSSID = wifiData.get(EngineUtils.SSID);
                Toast.makeText(mCurrentContext," Selected SSID " + mSelectedSSID , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mWifiSsidAdapter = new WifiSsidAdapter(getApplicationContext());
        mWifiListSpinner.setAdapter(mWifiSsidAdapter);

       // test();
    }

    @Override
    protected void onStart() {
        super.onStart();
        NetworkUtils.enableWifiConnection(mCurrentContext);

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
        navigateToDeviceListPage(true);
    }


    public void onConnectClick(View v){
        mPassword = mWifiPwd.getEditableText().toString().trim();
        showLoadingWifiConnection();
        mWifiConnection.startReceivingWifiChanges(getApplicationContext());
        mWifiConnection.ConnectToServiceSetID(getApplicationContext(),mSelectedSSID,mPassword,true);
        // get what is selected from spinner & pwd

    }


    private void scanForWifi(){
        showProgressWhileScanWifi();
        mWifiListData = mWifiPresenter.getWifiAccessPointsList();
        hideProgressOnScanDone();

        mWifiSsidAdapter.setData(mWifiListData);
        mWifiSsidAdapter.notifyDataSetChanged();
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
        String currentSsid = NetworkUtils.getCurrentSsid(WifiActivity.this);

        // remove double quoute from leading and trail
        currentSsid = currentSsid.replaceAll("\"","");

        if(mSelectedSSID.equalsIgnoreCase(currentSsid)){
            navigateToDeviceListPage(false);
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


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        mWifiConnection.stopReceivingWifiChanges();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * connection is successful , now based on the scenario, it will navigate to next page
     * @param isSkipped
     */
    private void navigateToDeviceListPage(boolean isSkipped) {
        if(mFromEditPage){
            // save the router credential in preferences
            AppPreferences.getInstance(WifiActivity.this).setRouterSSID(mSelectedSSID);
            AppPreferences.getInstance(WifiActivity.this).setRouterPassword(mPassword);
            AppPreferences.getInstance(WifiActivity.this).setRouter(true);

            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }else {
            if (!isSkipped) {
                // store ssid , password and router availability  in pref
                AppPreferences.getInstance(WifiActivity.this).setRouterSSID(mSelectedSSID);
                AppPreferences.getInstance(WifiActivity.this).setRouterPassword(mPassword);
                AppPreferences.getInstance(WifiActivity.this).setRouter(true);
            }

            Intent intent = new Intent(WifiActivity.this, DeviceListActivity.class);
            // Intent intent = new Intent(WifiActivity.this, DeviceControlListActivity.class);
            startActivity(intent);
        }
    }



    NsdHelper mNsdHelper;

    private void test(){
       /* TestUtility testUtility = new TestUtility();
        testUtility.TestDBInsert(WifiActivity.this);*/
        // print all the available device connected to current network

        ArrayList<String> macIdList = new ArrayList<>();
        macIdList.add("4C-66-41-2C-11-A0");
        AssociatedWifiHelper helper = new AssociatedWifiHelper(WifiActivity.this);
        // helper.setMacIds(macIdList);

       /* mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();

        mNsdHelper.registerService(7677);
        mNsdHelper.discoverServices();*/



    }

    @Override
    public void sniffStarted(String ipMask) {

    }

    @Override
    public void sniffCompleted(ConcurrentHashMap<String, String> map) {

    }
}
