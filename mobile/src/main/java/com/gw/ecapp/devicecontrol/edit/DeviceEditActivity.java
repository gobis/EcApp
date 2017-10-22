package com.gw.ecapp.devicecontrol.edit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.EditText;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.R;
import com.gw.ecapp.storage.model.DeviceModel;

import org.parceler.Parcels;

import butterknife.BindView;

/**
 * Created by iningosu on 10/22/2017.
 */

public class DeviceEditActivity extends Activity {



    @BindView(R.id.master_one_ch_container)
    View mMasterOneChContainer;

    @BindView(R.id.one_ch_device_name_edit)
    EditText mOneChDeviceName;

    @BindView(R.id.one_ch_device_save)
    Button mOneChDeviceSave;
    // Control -1
    @BindView(R.id.ch1_control_1_image)
    ImageView mOneChControlImage;
    @BindView(R.id.ch1_control_1_name_edit)
    EditText mOneChControlName;


    // Two Channel Device
    @BindView(R.id.master_two_ch_container)
    View mMasterTwoChContainer;

    @BindView(R.id.two_ch_device_name_edit)
    EditText mTwoChDeviceName;
    @BindView(R.id.two_ch_device_save)
    Button mTwoChDeviceSave;

    // Control -1
    @BindView(R.id.ch2_control_1_image)
    ImageView mTwoChControlOneImage;
    @BindView(R.id.ch2_control_1_name_edit)
    EditText mTwoChControlOneName;

    // Control -2
    @BindView(R.id.ch2_control_2_image)
    ImageView mTwoChControlTwoImage;
    @BindView(R.id.ch2_control_1_name_edit)
    EditText mTwoChControlTwoName;

    // Four Channel Device
    @BindView(R.id.master_four_ch_container)
    View mMasterFourChContainer;

    @BindView(R.id.four_ch_device_name_edit)
    EditText mFourChDeviceName;

    @BindView(R.id.four_ch_device_save)
    Button mFourChDeviceSave;

    // Control -1
    @BindView(R.id.ch4_control_1_image)
    ImageView mFourChControlOneImage;
    @BindView(R.id.ch4_control_1_name_edit)
    EditText mFourChControlOneName;

    // Control -2
    @BindView(R.id.ch4_control_2_image)
    ImageView mFourChControlTwoImage;
    @BindView(R.id.ch4_control_2_name_edit)
    EditText mFourChControlTwoName;

    // Control -3
    @BindView(R.id.ch4_control_3_image)
    ImageView mFourChControlThreeImage;
    @BindView(R.id.ch4_control_3_name_edit)
    EditText mFourChControlThreeName;

    // Control -4
    @BindView(R.id.ch4_control_4_image)
    ImageView mFourChControlFourImage;
    @BindView(R.id.ch4_control_4_name_edit)
    EditText mFourChControlFourName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_edit_item);

        DeviceModel deviceModel = Parcels.unwrap(getIntent().getParcelableExtra(AppConstant.Extras.Device));

        showRequiredContainer(deviceModel.getChannelCount());

    }

    @Override
    protected void onStart() {
        super.onStart();
        // call database and populate
        populateDeviceFromDb("");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    private void showRequiredContainer(int channelCount){

        switch (channelCount){
            case 1:
                mMasterOneChContainer.setVisibility(View.VISIBLE);
                mMasterTwoChContainer.setVisibility(View.GONE);
                mMasterFourChContainer.setVisibility(View.GONE);
                break;
            case 2:
                mMasterOneChContainer.setVisibility(View.GONE);
                mMasterTwoChContainer.setVisibility(View.VISIBLE);
                mMasterFourChContainer.setVisibility(View.GONE);
                break;
            case 4:
                mMasterOneChContainer.setVisibility(View.GONE);
                mMasterTwoChContainer.setVisibility(View.GONE);
                mMasterFourChContainer.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

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


    private void collectModifiedInfo(){

    }


    private void populateDeviceFromDb(String selectedDeviceMacId){

    }

}
