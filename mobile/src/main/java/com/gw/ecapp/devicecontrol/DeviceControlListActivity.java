package com.gw.ecapp.devicecontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gw.ecapp.NetworkUtils;
import com.gw.ecapp.R;

/**
 * Created by iningosu on 9/10/2017.
 */

public class DeviceControlListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.device_two_ch);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void onBtn1(View v) {

        boolean on = false;
        if (v.getTag(R.id.button4) == null) {
            on = true;
        }else{
            on =   !Boolean.parseBoolean(v.getTag(R.id.button4).toString());
        }
        v.setBackground(getDrawable(on?R.drawable.switch_on_icon:R.drawable.switch_off_icon));
        v.setTag(R.id.button4, on);

    }

    public void onBtn2(View v){
        boolean on = false;
        if (v.getTag(R.id.button4) == null) {
            on = true;
        }else{
            on =   !Boolean.parseBoolean(v.getTag(R.id.button4).toString());
        }
        v.setBackground(getDrawable(on?R.drawable.switch_on_icon:R.drawable.switch_off_icon));
        v.setTag(R.id.button4, on);

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
