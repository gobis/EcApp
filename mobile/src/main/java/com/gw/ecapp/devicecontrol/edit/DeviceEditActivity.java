package com.gw.ecapp.devicecontrol.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

import com.gw.ecapp.AppConstant;
import com.gw.ecapp.DialogManager;
import com.gw.ecapp.R;
import com.gw.ecapp.TwoButtonDialogListener;
import com.gw.ecapp.engine.udpEngine.events.MessageArrivedEvent;
import com.gw.ecapp.startup.WifiActivity;
import com.gw.ecapp.storage.AppPreferences;
import com.gw.ecapp.storage.DatabaseManager;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;
import com.gw.ecapp.utility.StationModeModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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

public class DeviceEditActivity extends AppCompatActivity {


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
    private ActionBar mAppBar;

    private DeviceEditPresenter mPresenter;

    private Context mCurrentContext;

    private static final int ROUTER_DATA = 1298;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_edit_item);
        mSelectedDeviceModel = Parcels.unwrap(getIntent().getParcelableExtra(AppConstant.Extras.Device));

        ButterKnife.bind(this);
        try {
            showRequiredContainer(mSelectedDeviceModel);
        } catch (Exception e) {
            Log.e(TAG, "Exception occured " + e.toString());
        }

        mCurrentContext = DeviceEditActivity.this;
        mAppBar = getSupportActionBar();
        mAppBar.setTitle(getString(R.string.edit_device));

        mPresenter = new DeviceEditPresenter(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showRequiredContainer(DeviceModel deviceModel) throws Exception {
        switch (deviceModel.getChannelCount()) {
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


    private void showOneChannelInfo(DeviceModel deviceModel) throws Exception {

        mOneChDeviceName.setText(deviceModel.getDeviceName());
        ArrayList<ApplianceModel> applianceModels = deviceModel.getConnectedDevices();
        Log.i(TAG, " Appliance count should be one : Check Count =>  " + applianceModels.size());
        mOneChControlName.setText(applianceModels.get(0).getDeviceName());
    }

    private void showTwoChannelInfo(DeviceModel deviceModel) throws Exception {

        mTwoChDeviceName.setText(deviceModel.getDeviceName());
        ArrayList<ApplianceModel> applianceModels = deviceModel.getConnectedDevices();
        Log.i(TAG, " Appliance count should be two : Check Count =>  " + applianceModels.size());
        mTwoChControlOneName.setText(applianceModels.get(0).getDeviceName());
        mTwoChControlTwoName.setText(applianceModels.get(1).getDeviceName());

    }

    private void showFourChannelInfo(DeviceModel deviceModel) throws Exception {
        mFourChDeviceName.setText(deviceModel.getDeviceName());
        ArrayList<ApplianceModel> applianceModels = deviceModel.getConnectedDevices();
        Log.i(TAG, " Appliance count should be four : Check Count =>  " + applianceModels.size());

        mFourChControlOneName.setText(applianceModels.get(0).getDeviceName());
        mFourChControlTwoName.setText(applianceModels.get(1).getDeviceName());
        mFourChControlThreeName.setText(applianceModels.get(2).getDeviceName());
        mFourChControlFourName.setText(applianceModels.get(3).getDeviceName());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.device_edit_menu, menu);
        return true;
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_station_mode:
                //  show popup with wifi-router info , where user going to connect
                // if yes, connect all the devices with router info
                // else, show the first page
                if (AppPreferences.getInstance(DeviceEditActivity.this).hasRouter()) {
                    showStationModeDialog();
                } else {
                    showWifiDialog();
                }

                break;
            default:
                break;
        }

        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageArrivedEvent msgArriveEvent) {
        Log.i(TAG, "on Event called :: onEventMainThread info " + msgArriveEvent.message.toString());
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


    @OnClick(R.id.one_ch_device_save)
    public void saveOneChannelInfo() {

        String updatedDeviceName = mOneChDeviceName.getEditableText().toString();
        String updatedControlOneName = mOneChControlName.getEditableText().toString();

        List<DeviceModel> deviceModelList = new ArrayList<>();
        DeviceModel deviceModel1 = mSelectedDeviceModel.getDeepCopy();

        deviceModel1.setDeviceName(updatedDeviceName);
        deviceModel1.setApplianceName(updatedControlOneName);

        deviceModelList.add(deviceModel1);

        persistUpdatedInfo(deviceModelList);

    }

    @OnClick(R.id.two_ch_device_save)
    public void saveTwoChannelInfo() {

        String updatedDeviceName = mTwoChDeviceName.getEditableText().toString();
        String updatedControlOneName = mTwoChControlOneName.getEditableText().toString();
        String updatedControlTwoName = mTwoChControlTwoName.getEditableText().toString();

        List<DeviceModel> deviceModelList = new ArrayList<>();
        DeviceModel deviceModel1 = mSelectedDeviceModel.getDeepCopy();
        DeviceModel deviceModel2 = mSelectedDeviceModel.getDeepCopy();

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

        List<DeviceModel> deviceModelList = new ArrayList<>();
        DeviceModel deviceModel1 = mSelectedDeviceModel.getDeepCopy();
        DeviceModel deviceModel2 = mSelectedDeviceModel.getDeepCopy();
        DeviceModel deviceModel3 = mSelectedDeviceModel.getDeepCopy();
        DeviceModel deviceModel4 = mSelectedDeviceModel.getDeepCopy();

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


    private void onConfigSaved() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }


    /**
     * user has provided router name and password already
     * now, he wants to connect all devices using this credentials
     * display credentials before connecting it
     * this is not only for cross verification, also serves the purpose of password change
     */
    private void showStationModeDialog() {

        final String ssid = AppPreferences.getInstance(mCurrentContext).getRouterSSID();
        final String password = AppPreferences.getInstance(mCurrentContext).getRouterPassword();

        DialogManager.showGenericConfirmDialogForTwoButtons(
                mCurrentContext,
                getString(R.string.station_mode_confirm_msg, ssid, password),
                getString(R.string.yes), getString(R.string.change_password), new TwoButtonDialogListener() {

                    @Override
                    public void positiveButtonClicked() {
                        // start setting station mode
                        StationModeModel stationModeModel = new StationModeModel();
                        stationModeModel.setSsid(ssid);
                        stationModeModel.setPassword(password);
                        mPresenter.moveToStationMode(stationModeModel, mSelectedDeviceModel.getDeviceName());

                        // connect to device one by one and send the station mode command
                        Toast.makeText(mCurrentContext,
                                "Moving to station mode", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void negativeButtonClicked() {
                        // user password has changed , take user to wifi page


                    }
                });

    }


    /**
     * show this dialog if user doesn't have wifi router and want to connect with new router
     */
    private void showWifiDialog() {

        DialogManager.showGenericConfirmDialogForTwoButtons(
                mCurrentContext,
                getString(R.string.no_router_info), getString(R.string.yes), getString(R.string.no), new TwoButtonDialogListener() {

                    @Override
                    public void positiveButtonClicked() {
                        // take user to first page
                        navigateToWifiRouterPage();

                    }

                    @Override
                    public void negativeButtonClicked() {
                        // user don't want to provide info , continue as AP mode

                    }
                });

    }

    /**
     * take to user to wifi Router page
     */
    public void navigateToWifiRouterPage() {
        Intent intent = new Intent(DeviceEditActivity.this, WifiActivity.class);
        intent.putExtra(AppConstant.Extras.FROM_DEVICE_EDIT_PAGE, true);
        startActivityForResult(intent, ROUTER_DATA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ROUTER_DATA) {
                String ssid = AppPreferences.getInstance(DeviceEditActivity.this).getRouterSSID();
                String password = AppPreferences.getInstance(DeviceEditActivity.this).getRouterPassword();
                Log.i(TAG, " Got Router Data  SSID " + ssid + " Password " + password);
                moveToStationMode(ssid,password);
            }
        }
    }

    /**
     * responsible to send the ssid & password to device
     * @param ssid
     * @param password
     */
    private void moveToStationMode(String ssid, String password) {
        StationModeModel stationModeModel = new StationModeModel();
        stationModeModel.setSsid(ssid);
        stationModeModel.setPassword(password);
        mPresenter.moveToStationMode(stationModeModel, mSelectedDeviceModel.getDeviceName());
    }

    /**
     * call back when station mode success
     */
    public void stationModeSuccess() {

    }

    /**
     * call back when station mode failed
     */
    public void stationModeFailed() {

        // connect to device one by one and send the station mode command
        Toast.makeText(mCurrentContext,
                getString(R.string.st_mode_fail), Toast.LENGTH_SHORT).show();
    }


}
