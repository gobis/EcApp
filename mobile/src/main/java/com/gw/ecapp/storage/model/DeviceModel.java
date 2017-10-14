package com.gw.ecapp.storage.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;

/**
 * Created by iningosu on 10/9/2017.
 */

public class DeviceModel {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String deviceName;

    @DatabaseField
    public String devicePassword;

    @DatabaseField
    public String modelNumber;
    @DatabaseField
    public String channelCount;
    @DatabaseField
    public String macId;
    @DatabaseField
    public String mPreferredIP;
    @DatabaseField
    public String mLastConnectedIP;

    // Name given by the end user
    @DatabaseField
    public String configureName;

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

    public String getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(String channelCount) {
        this.channelCount = channelCount;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getmPreferredIP() {
        return mPreferredIP;
    }

    public void setmPreferredIP(String mPreferredIP) {
        this.mPreferredIP = mPreferredIP;
    }

    public String getmLastConnectedIP() {
        return mLastConnectedIP;
    }

    public void setmLastConnectedIP(String mLastConnectedIP) {
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

    public int getDeviceId() {
        return id;
    }

    public void setDeviceId(int deviceId) {
        this.id = deviceId;
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
                ", EXTRA_COL1='" + EXTRA_COL1 + '\'' +
                ", EXTRA_COL2='" + EXTRA_COL2 + '\'' +
                '}';
    }
}