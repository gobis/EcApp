package com.gw.ecapp.demo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gw.ecapp.AppUtils;
import com.gw.ecapp.R;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.EngineUtils;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPClient;
import com.gw.ecapp.engine.udpEngine.udpComms.UDPRequestStatus;

/**
 * Created by iningosu on 8/26/2017.
 */

public class DemoActivity extends Activity implements UDPRequestStatus {

    UDPClient mEngine;

    EditText mCommand;
    EditText mIpAddress;
    EditText mPortNumber;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_layout);

        mCommand = (EditText)findViewById(R.id.command);
        mIpAddress = (EditText)findViewById(R.id.udp_ip);
        mPortNumber = (EditText)findViewById(R.id.udp_port);


    }

    @Override
    protected void onStart() {
        super.onStart();
        createConnectionWithEngine();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void createConnectionWithEngine(){
        mEngine = (UDPClient)CommEngine.getCommsEngine(DemoActivity.this, CommEngine.ENGINE_TYPE.UDP,this);
    }

    public void onCommandSendClick(View v) {

        String ipAddress = mIpAddress.getEditableText().toString();
        String portNumber = mPortNumber.getEditableText().toString();

        if(AppUtils.validateIp(ipAddress)) {
            if (null == EngineUtils.UDP_UNI_CAST_IP || EngineUtils.UDP_UNI_CAST_IP.isEmpty()) {
                EngineUtils.setUdpUniCastIp(ipAddress, Integer.parseInt(portNumber));
            }

            String cmd = mCommand.getEditableText().toString();

            if (null != cmd && !cmd.isEmpty()) {
                mEngine.sendMessageToDevice(cmd);
            }
        }else{
            Toast.makeText(DemoActivity.this," Not valid IP",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void requestSuccess() {

    }

    @Override
    public void requestTimeOut() {

    }

    @Override
    public void requestRetryCount(int retryCount) {

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
    public void onBackPressed() {
        DemoActivity.this.moveTaskToBack(true);
    }

}
