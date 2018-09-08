package com.gw.ecapp.storage.model;

import com.j256.ormlite.field.DatabaseField;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by iningosu on 10/9/2017.
 */

@Parcel
public class DeviceModel {


    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String deviceName;

    @DatabaseField
    public String deviceSsid;

    @DatabaseField
    public String devicePassword;

    @DatabaseField
    public String modelNumber;

    @DatabaseField
    public int channelCount;

    @DatabaseField
    public int connMode;

    // this is transient, no need to persist
    public boolean isFoundInNwSniff;

    @DatabaseField
    public String macId;

    @DatabaseField
    public String mPreferredIP;
    @DatabaseField
    public String mLastConnectedIP;

    // Name given by the end user
    @DatabaseField
    public String configureName;

    @DatabaseField
    public String applianceName;

    @DatabaseField
    public String relayNumber;

    public ArrayList<ApplianceModel> connectedDevices;

    @DatabaseField
    public String EXTRA_COL1;

    @DatabaseField
    public String EXTRA_COL2;


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevicePassword() {
        return devicePassword;
    }

    public void setDevicePassword(String devicePassword) {
        this.devicePassword = devicePassword;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getPreferredIP() {
        return mPreferredIP;
    }

    public void setPreferredIP(String mPreferredIP) {
        this.mPreferredIP = mPreferredIP;
    }

    public String getLastConnectedIP() {
        return mLastConnectedIP;
    }

    public void setLastConnectedIP(String mLastConnectedIP) {
        this.mLastConnectedIP = mLastConnectedIP;
    }

    public String getConfigureName() {
        return configureName;
    }

    public void setConfigureName(String configureName) {
        this.configureName = configureName;
    }

    public ArrayList<ApplianceModel> getConnectedDevices() {
        return connectedDevices;
    }

    public void setConnectedDevices(ArrayList<ApplianceModel> connectedDevices) {
        this.connectedDevices = connectedDevices;
    }

    public String getEXTRA_COL1() {
        return EXTRA_COL1;
    }

    public void setEXTRA_COL1(String EXTRA_COL1) {
        this.EXTRA_COL1 = EXTRA_COL1;
    }

    public String getEXTRA_COL2() {
        return EXTRA_COL2;
    }

    public void setEXTRA_COL2(String EXTRA_COL2) {
        this.EXTRA_COL2 = EXTRA_COL2;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }

    public String getRelayNumber() {
        return relayNumber;
    }

    public void setRelayNumber(String relayNumber) {
        this.relayNumber = relayNumber;
    }


    public String getDeviceSsid() {
        return deviceSsid;
    }

    public void setDeviceSsid(String deviceSsid) {
        this.deviceSsid = deviceSsid;
    }

    public int getConnMode() {
        return connMode;
    }

    public void setConnMode(int connMode) {
        this.connMode = connMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public DeviceModel getDeepCopy() {
        DeviceModel deviceModel = new DeviceModel();

        deviceModel.setMacId(this.getMacId());
        deviceModel.setConfigureName(this.getConfigureName());
        deviceModel.setDeviceName(this.getDeviceName());
        deviceModel.setApplianceName(this.getApplianceName());
        deviceModel.setDeviceSsid(this.getDeviceSsid());
        deviceModel.setDevicePassword(this.getDevicePassword());
        deviceModel.setChannelCount(this.getChannelCount());
        deviceModel.setEXTRA_COL1(this.getEXTRA_COL1());
        deviceModel.setEXTRA_COL2(this.getEXTRA_COL2());
        deviceModel.setRelayNumber(this.getRelayNumber());
        deviceModel.setPreferredIP(this.getPreferredIP());
        deviceModel.setLastConnectedIP(this.getLastConnectedIP());

        int connectedDeviceCount = this.getConnectedDevices().size();

        ArrayList<ApplianceModel> applianceModels = new ArrayList<>();

        for (int i = 0; i < connectedDeviceCount; i++) {
            ApplianceModel applianceModel = new ApplianceModel();
            applianceModel.setDeviceName(this.getConnectedDevices().get(i).getDeviceName());
            applianceModel.setRelayNumber(this.getConnectedDevices().get(i).getRelayNumber());
            applianceModel.setDeviceMacId(this.getConnectedDevices().get(i).getDeviceMacId());
            applianceModel.setId(this.getConnectedDevices().get(i).getId());
            applianceModels.add(applianceModel);
        }

        deviceModel.setConnectedDevices(applianceModels);

        return deviceModel;
    }


    @Override
    public String toString() {
        return "DeviceModel{" +
                "id='" + id + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", devicePassword='" + devicePassword + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", channelCount='" + channelCount + '\'' +
                ", macId='" + macId + '\'' +
                ", mPreferredIP='" + mPreferredIP + '\'' +
                ", mLastConnectedIP='" + mLastConnectedIP + '\'' +
                ", configureName='" + configureName + '\'' +
                ", applianceName='" + applianceName + '\'' +
                ", relayNumber='" + relayNumber + '\'' +
                ", EXTRA_COL1='" + EXTRA_COL1 + '\'' +
                ", EXTRA_COL2='" + EXTRA_COL2 + '\'' +
                '}';
    }
}
