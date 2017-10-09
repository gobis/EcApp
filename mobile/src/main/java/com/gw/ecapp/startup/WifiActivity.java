package com.gw.ecapp.startup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gw.ecapp.AssociatedWifiHelper;
import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;
import com.gw.ecapp.TestUtility;
import com.gw.ecapp.WifiConnection;
import com.gw.ecapp.configuration.DeviceListActivity;
import com.gw.ecapp.engine.udpEngine.EngineUtils;
import com.gw.ecapp.startup.SpinnerAdapter.WifiSsidAdapter;
import com.gw.ecapp.storage.AppPreferences;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by iningosu on 8/27/2017.
 */

public class WifiActivity extends Activity implements WifiConnection.ConnectionStatusInterface {


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

        test();
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

    }


    public void onConnectClick(View v){
        mPassword = mWifiPwd.getEditableText().toString().trim();
        showLoadingWifiConnection();
        mWifiConnection.startReceivingWifiChanges(getApplicationContext());
        mWifiConnection.ConnectToServiceSetID(getApplicationContext(),mSelectedSSID,mPassword);
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
            navigateToDeviceListPage();
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


    private void navigateToDeviceListPage(){

        // print all the available device connected to current network

        AssociatedWifiHelper helper = new AssociatedWifiHelper();
        helper.getAssociatedWifi(WifiActivity.this);

        // store ssid , password and router availability  in pref

        AppPreferences.getInstance(WifiActivity.this).setRouterSSID(mSelectedSSID);
        AppPreferences.getInstance(WifiActivity.this).setRouterPassword(mPassword);
        AppPreferences.getInstance(WifiActivity.this).setRouter(true);


        Intent intent = new Intent(WifiActivity.this, DeviceListActivity.class);
        startActivity(intent);
    }


    private void test(){
        TestUtility testUtility = new TestUtility();
        testUtility.TestDBInsert(WifiActivity.this);
    }


}
