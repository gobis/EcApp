package com.gw.ecapp.devicecontrol.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.Toast;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.R;
import com.gw.ecapp.storage.DatabaseManager;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by iningosu on 10/22/2017.
 */

public class DeviceEditActivity extends Activity {


    private String TAG = getClass().getSimpleName();


    // One Channel Device
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
    @BindView(R.id.ch2_control_2_name_edit)
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


    private DeviceModel mSelectedDeviceModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_edit_item);
        mSelectedDeviceModel = Parcels.unwrap(getIntent().getParcelableExtra(AppConstant.Extras.Device));

        ButterKnife.bind(this);
        try {
            showRequiredContainer(mSelectedDeviceModel);
        }catch (Exception e){
            Log.e(TAG,"Exception Occured " + e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showRequiredContainer(DeviceModel deviceModel) throws Exception{
        switch (deviceModel.getChannelCount()){
            case 1:
                mMasterOneChContainer.setVisibility(View.VISIBLE);
                mMasterTwoChContainer.setVisibility(View.GONE);
                mMasterFourChContainer.setVisibility(View.GONE);
                showOneChannelInfo(deviceModel);
                break;
            case 2:
                mMasterOneChContainer.setVisibility(View.GONE);
                mMasterTwoChContainer.setVisibility(View.VISIBLE);
                mMasterFourChContainer.setVisibility(View.GONE);
                showTwoChannelInfo(deviceModel);
                break;
            case 4:
                mMasterOneChContainer.setVisibility(View.GONE);
                mMasterTwoChContainer.setVisibility(View.GONE);
                mMasterFourChContainer.setVisibility(View.VISIBLE);
                showFourChannelInfo(deviceModel);
                break;
            default:
                break;
        }
    }


    private void showOneChannelInfo(DeviceModel deviceModel) throws Exception{

        mOneChDeviceName.setText(deviceModel.getDeviceName());
        ArrayList<ApplianceModel> applianceModels = deviceModel.getConnectedDevices();
        Log.i(TAG," Appliance count should be one : Check Count =>  " + applianceModels.size());
        mOneChControlName.setText(applianceModels.get(0).getDeviceName());
    }

    private void showTwoChannelInfo(DeviceModel deviceModel) throws Exception{

        mTwoChDeviceName.setText(deviceModel.getDeviceName());
        ArrayList<ApplianceModel> applianceModels = deviceModel.getConnectedDevices();
        Log.i(TAG," Appliance count should be two : Check Count =>  " + applianceModels.size());
        mTwoChControlOneName.setText(applianceModels.get(0).getDeviceName());
        mTwoChControlTwoName.setText(applianceModels.get(1).getDeviceName());

    }

    private void showFourChannelInfo(DeviceModel deviceModel) throws Exception{
        mFourChDeviceName.setText(deviceModel.getDeviceName());
        ArrayList<ApplianceModel> applianceModels = deviceModel.getConnectedDevices();
        Log.i(TAG," Appliance count should be four : Check Count =>  " + applianceModels.size());

        mFourChControlOneName.setText(applianceModels.get(0).getDeviceName());
        mFourChControlTwoName.setText(applianceModels.get(1).getDeviceName());
        mFourChControlThreeName.setText(applianceModels.get(2).getDeviceName());
        mFourChControlFourName.setText(applianceModels.get(3).getDeviceName());

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


    @OnClick(R.id.one_ch_device_save)
    public void saveOneChannelInfo(){

        String updatedDeviceName = mOneChDeviceName.getEditableText().toString();
        String updatedControlOneName = mOneChControlName.getEditableText().toString();

        List<DeviceModel>  deviceModelList = new ArrayList<>();
        DeviceModel deviceModel1 =  mSelectedDeviceModel.getDeepCopy();

        deviceModel1.setDeviceName(updatedDeviceName);
        deviceModel1.setApplianceName(updatedControlOneName);

        deviceModelList.add(deviceModel1);

        persistUpdatedInfo(deviceModelList);

    }

    @OnClick(R.id.two_ch_device_save)
    public void saveTwoChannelInfo(){

        String updatedDeviceName = mTwoChDeviceName.getEditableText().toString();
        String updatedControlOneName = mTwoChControlOneName.getEditableText().toString();
        String updatedControlTwoName = mTwoChControlTwoName.getEditableText().toString();

        List<DeviceModel>  deviceModelList = new ArrayList<>();
        DeviceModel deviceModel1 =  mSelectedDeviceModel.getDeepCopy();
        DeviceModel deviceModel2 =  mSelectedDeviceModel.getDeepCopy();

        deviceModel1.setDeviceName(updatedDeviceName);
        deviceModel1.setApplianceName(updatedControlOneName);

        deviceModel2.setDeviceName(updatedDeviceName);
        deviceModel2.setApplianceName(updatedControlTwoName);

        deviceModelList.add(deviceModel1);
        deviceModelList.add(deviceModel2);

        persistUpdatedInfo(deviceModelList);


    }

    @OnClick(R.id.four_ch_device_save)
    public void saveFourChannelInfo() {

        String updatedDeviceName = mFourChDeviceName.getEditableText().toString();
        String updatedControlOneName = mFourChControlOneName.getEditableText().toString();
        String updatedControlTwoName = mFourChControlTwoName.getEditableText().toString();
        String updatedControlThreeName = mFourChControlThreeName.getEditableText().toString();
        String updatedControlFourName = mFourChControlFourName.getEditableText().toString();

        List<DeviceModel>  deviceModelList = new ArrayList<>();
        DeviceModel deviceModel1 =  mSelectedDeviceModel.getDeepCopy();
        DeviceModel deviceModel2 =  mSelectedDeviceModel.getDeepCopy();
        DeviceModel deviceModel3 =  mSelectedDeviceModel.getDeepCopy();
        DeviceModel deviceModel4 =  mSelectedDeviceModel.getDeepCopy();

        deviceModel1.setDeviceName(updatedDeviceName);
        deviceModel1.setApplianceName(updatedControlOneName);
        deviceModel1.setRelayNumber("1");

        deviceModel2.setDeviceName(updatedDeviceName);
        deviceModel2.setApplianceName(updatedControlTwoName);
        deviceModel2.setRelayNumber("2");

        deviceModel3.setDeviceName(updatedDeviceName);
        deviceModel3.setApplianceName(updatedControlThreeName);
        deviceModel3.setRelayNumber("3");

        deviceModel4.setDeviceName(updatedDeviceName);
        deviceModel4.setApplianceName(updatedControlFourName);
        deviceModel3.setRelayNumber("4");

        deviceModelList.add(deviceModel1);
        deviceModelList.add(deviceModel2);
        deviceModelList.add(deviceModel3);
        deviceModelList.add(deviceModel4);

        persistUpdatedInfo(deviceModelList);

    }

    private void persistUpdatedInfo(List<DeviceModel> deviceModelList) {

        DatabaseManager dbManager = DatabaseManager.getInstance(DeviceEditActivity.this);
        dbManager.bulkUpdateDevice(deviceModelList)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        onConfigSaved();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(DeviceEditActivity.this, "Config is not saved, try again / report", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void onConfigSaved(){
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }


}
