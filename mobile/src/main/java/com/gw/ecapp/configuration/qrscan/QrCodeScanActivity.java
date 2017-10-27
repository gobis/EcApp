package com.gw.ecapp.configuration.qrscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.Result;
import com.gw.ecapp.AppConfig;
import com.gw.ecapp.AppConstant;
import com.gw.ecapp.R;
import com.gw.ecapp.configuration.qrscan.model.QrCodeData;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by iningosu on 10/27/2017.
 */

public class QrCodeScanActivity extends Activity
               implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String TAG = getClass().getSimpleName();


    private String mSsid ;

    private Handler mUiHandler ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        mUiHandler = new Handler();

        mSsid = getIntent().getStringExtra(AppConstant.Extras.DEVICE_SSID);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }


    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        Log.v(TAG, result.getText()); // Prints scan results
        // Prints the scan format (qrcode, pdf417 etc.)
        // json format  {"ssid" : "GK793784" , "password" : "12345678"}
        Log.v(TAG, result.getBarcodeFormat().toString());
        parseResponse(result.getText());
    }

    /**
     * parsing response from scan result
     * @param data
     */
    private void parseResponse(String data) {
        try {
            Gson gson = new Gson();
            QrCodeData qrCodeData = gson.fromJson(data, QrCodeData.class);

            if (qrCodeData.getSsid().equalsIgnoreCase(mSsid)) {
                onScanSuccessful(qrCodeData.getPassword());
            } else {
                Snackbar snackbar = Snackbar
                        .make(mScannerView, getString(R.string.check_qrcode), Snackbar.LENGTH_LONG);
                snackbar.show();

                mUiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScannerView.resumeCameraPreview(QrCodeScanActivity.this);
                    }
                }, AppConfig.QRCORE_TRY_INTERVAL);

            }

        } catch (JsonSyntaxException e) {
            Log.e(TAG, "JsonSyntaxException" + e.toString());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void onScanSuccessful(String devicePassword){
        Intent intent = new Intent();
        intent.putExtra(AppConstant.Extras.DEVICE_PWD , devicePassword);
        setResult(RESULT_OK,intent);
        finish();
    }

}
