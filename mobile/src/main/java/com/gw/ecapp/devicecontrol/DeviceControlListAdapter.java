package com.gw.ecapp.devicecontrol;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.gw.ecapp.R;
import com.gw.ecapp.storage.model.ApplianceModel;
import com.gw.ecapp.storage.model.DeviceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iningosu on 10/14/2017.
 */

public class DeviceControlListAdapter extends RecyclerView.Adapter<DeviceControlListAdapter.MyViewHolder> {

    private Context mContext;

    private List<DeviceModel> mDeviceList;

    String TAG = getClass().getSimpleName();

    public DeviceControlListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<DeviceModel> deviceList) {
        mDeviceList = deviceList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        // have to define all the views for 1,2 & 4 channel
        // have to analyze better solution
        View mMasterOneChContainer;
        TextView mOneChDeviceName;
        ImageView mOneChEdit;
        // Control -1
        ImageView mOneChControlImage;
        TextView mOneChControlName;
        Switch mOneChControlSwitch;


        // Two Channel Device
        View mMasterTwoChContainer;

        TextView mTwoChDeviceName;
        ImageView mTwoChEdit;

        // Control -1
        ImageView mTwoChControlOneImage;
        TextView mTwoChControlOneName;
        Switch mTwoChControlOneSwitch;

        // Control -2
        ImageView mTwoChControlTwoImage;
        TextView mTwoChControlTwoName;
        Switch mTwoChControlTwoSwitch;

        // Four Channel Device
        View mMasterFourChContainer;

        TextView mFourChDeviceName;
        ImageView mFourChEdit;

        // Control -1
        ImageView mFourChControlOneImage;
        TextView mFourChControlOneName;
        Switch mFourChControlOneSwitch;

        // Control -2
        ImageView mFourChControlTwoImage;
        TextView mFourChControlTwoName;
        Switch mFourChControlTwoSwitch;

        // Control -3
        ImageView mFourChControlThreeImage;
        TextView mFourChControlThreeName;
        Switch mFourChControlThreeSwitch;

        // Control -4
        ImageView mFourChControlFourImage;
        TextView mFourChControlFourName;
        Switch mFourChControlFourSwitch;


        public MyViewHolder(View view) {
            super(view);
            mMasterOneChContainer = (View) view.findViewById(R.id.master_one_ch_container);
            mOneChDeviceName = (TextView) view.findViewById(R.id.one_ch_device_name);
            mOneChEdit = (ImageView) view.findViewById(R.id.ch_1_edit);
            // control Name
            mOneChControlImage = (ImageView) view.findViewById(R.id.ch1_control_1_image);
            mOneChControlName = (TextView) view.findViewById(R.id.ch1_control_1_name);
            mOneChControlSwitch = (Switch) view.findViewById(R.id.ch1_control_1_switch_1);


            mMasterTwoChContainer = (View) view.findViewById(R.id.master_two_ch_container);
            mTwoChDeviceName = (TextView) view.findViewById(R.id.two_ch_device_name);
            mTwoChEdit = (ImageView) view.findViewById(R.id.ch_2_edit);
            // control -1 info
            mTwoChControlOneImage = (ImageView) view.findViewById(R.id.ch2_control_1_image);
            mTwoChControlOneName = (TextView) view.findViewById(R.id.ch2_control_1_name);
            mTwoChControlOneSwitch = (Switch) view.findViewById(R.id.ch_2_control_1_switch);

            // control -2 info
            mTwoChControlTwoImage = (ImageView) view.findViewById(R.id.ch2_control_2_image);
            mTwoChControlTwoName = (TextView) view.findViewById(R.id.ch2_control_2_name);
            mTwoChControlTwoSwitch = (Switch) view.findViewById(R.id.ch_2_control_2_switch);

            mMasterFourChContainer = (View) view.findViewById(R.id.master_four_ch_container);
            mFourChDeviceName = (TextView) view.findViewById(R.id.four_ch_device_name);
            mFourChEdit = (ImageView) view.findViewById(R.id.ch4_edit);
            // control -1 info
            mFourChControlOneImage = (ImageView) view.findViewById(R.id.ch4_control_1_image);
            mFourChControlOneName = (TextView) view.findViewById(R.id.ch4_control_1_name);
            mFourChControlOneSwitch = (Switch) view.findViewById(R.id.ch4_control_1_switch);

            // control -2 info
            mFourChControlTwoImage = (ImageView) view.findViewById(R.id.ch4_control_2_image);
            mFourChControlTwoName = (TextView) view.findViewById(R.id.ch4_control_2_name);
            mFourChControlTwoSwitch = (Switch) view.findViewById(R.id.ch4_control_2_switch);

            // control -3 info
            mFourChControlThreeImage = (ImageView) view.findViewById(R.id.ch4_control_3_image);
            mFourChControlThreeName = (TextView) view.findViewById(R.id.ch4_control_3_name);
            mFourChControlThreeSwitch = (Switch) view.findViewById(R.id.ch4_control_3_switch);

            // control -4 info
            mFourChControlFourImage = (ImageView) view.findViewById(R.id.ch4_control_4_image);
            mFourChControlFourName = (TextView) view.findViewById(R.id.ch4_control_4_name);
            mFourChControlFourSwitch = (Switch) view.findViewById(R.id.ch4_control_4_switch);


        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_control_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder , int position) {

        DeviceModel deviceModel = mDeviceList.get(position);

        switch (deviceModel.channelCount) {

            case 1:
                showOneChannelControl(holder,deviceModel);
                break;
            case 2:
                showTwoChannelControl(holder,deviceModel);
                break;
            case 4:
                showFourChannelControl(holder,deviceModel);
                break;
            case 8:
                break;
            default:
                break;

        }

    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }


    private void showOneChannelControl(MyViewHolder holder, DeviceModel deviceModel) {

        holder.mMasterFourChContainer.setVisibility(View.GONE);
        holder.mMasterTwoChContainer.setVisibility(View.GONE);
        holder.mMasterOneChContainer.setVisibility(View.VISIBLE);

        ArrayList<ApplianceModel> connectedDevices = deviceModel.getConnectedDevices();
        holder.mOneChDeviceName.setText(deviceModel.getDeviceName());
        if(connectedDevices.size() > 0 ){
            ApplianceModel applianceModel = connectedDevices.get(0);
            holder.mOneChControlName.setText(applianceModel.getDeviceName());
        }

    }

    private void showTwoChannelControl(MyViewHolder holder, DeviceModel deviceModel) {
        holder.mMasterFourChContainer.setVisibility(View.GONE);
        holder.mMasterTwoChContainer.setVisibility(View.VISIBLE);
        holder.mMasterOneChContainer.setVisibility(View.GONE);

        holder.mTwoChDeviceName.setText(deviceModel.getDeviceName());
        ArrayList<ApplianceModel> connectedDevices = deviceModel.getConnectedDevices();
        try {
            ApplianceModel applianceModel = connectedDevices.get(0);
            holder.mTwoChControlOneName.setText(applianceModel.getDeviceName());

            ApplianceModel applianceModel2 = connectedDevices.get(1);
            holder.mTwoChControlOneName.setText(applianceModel2.getDeviceName());

        }catch (Exception e){
            Log.e(TAG," Exception "+e);
        }

    }

    private void showFourChannelControl(MyViewHolder holder ,DeviceModel deviceModel) {
        holder.mMasterFourChContainer.setVisibility(View.VISIBLE);
        holder.mMasterTwoChContainer.setVisibility(View.GONE);
        holder.mMasterOneChContainer.setVisibility(View.GONE);

        holder.mFourChDeviceName.setText(deviceModel.getDeviceName());
        ArrayList<ApplianceModel> connectedDevices = deviceModel.getConnectedDevices();
        try {
            ApplianceModel applianceModel = connectedDevices.get(0);
            holder.mFourChControlOneName.setText(applianceModel.getDeviceName());

            ApplianceModel applianceModel2 = connectedDevices.get(1);
            holder.mFourChControlTwoName.setText(applianceModel2.getDeviceName());

            ApplianceModel applianceModel3 = connectedDevices.get(2);
            holder.mFourChControlThreeName.setText(applianceModel3.getDeviceName());

            ApplianceModel applianceModel4 = connectedDevices.get(3);
            holder.mFourChControlFourName.setText(applianceModel4.getDeviceName());

        }catch (Exception e){
            Log.e(TAG," Exception "+e);
        }

    }

}
