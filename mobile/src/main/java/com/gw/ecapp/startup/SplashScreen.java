package com.gw.ecapp.startup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.R;
import com.gw.ecapp.devicecontrol.DeviceControlListActivity;
import com.gw.ecapp.storage.AppPreferences;

/**
 * very first screen, it asks for permission
 *  if already configured, then navigate to device list activity
 *   if not configured then navigate to wifi activity
 */
public class SplashScreen extends Activity {

    private static final int REQ_PERMISSION_CODE = 124;

    String[] permissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        permissions = new String[]{
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.CAMERA
        };


        if(!hasPermission()){
            requestPermission();
        }else{
            navigateToNextPage();
        }

    }


    /**
     * checking for required permission
     * @return
     */
    public boolean hasPermission(){
        boolean hasAllPermission = true ;

        for (String permission:permissions ) {
            int result = checkCallingOrSelfPermission(permission);
            if(result != PackageManager.PERMISSION_GRANTED){
                hasAllPermission = false ;
            }
        }
        return hasAllPermission ;
    }


    /**
     * requesting permission if required
     */
    public void requestPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // have to check for the permission
            requestPermissions(permissions,REQ_PERMISSION_CODE);
        }else{

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         boolean permissionGranted = true ;

        switch (requestCode){
            case REQ_PERMISSION_CODE:

                for (int result : grantResults) {
                    permissionGranted = permissionGranted && (result == PackageManager.PERMISSION_GRANTED);
                }

                break;

            default:
                permissionGranted = false ;
                break;

        }

        checkForPermissions(permissionGranted);

    }


    /**
     * check for the permission
     * @param permissionGranted
     */
    private void checkForPermissions(boolean permissionGranted){
        if(permissionGranted){
            navigateToNextPage();
        }else{
            Toast.makeText(this,"You dont have enough permission to continue the applciation",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * responsible to navigate to next page
     */
    private void navigateToNextPage(){

        if(AppPreferences.getInstance(SplashScreen.this).getConfigStatus()){
            Intent intent = new Intent(this,DeviceControlListActivity.class);
            intent.putExtra(AppConstant.Extras.SCAN_LAN,true);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this,WifiActivity.class);
            startActivity(intent);
        }
    }






}
