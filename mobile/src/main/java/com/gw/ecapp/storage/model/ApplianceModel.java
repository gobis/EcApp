package com.gw.ecapp.storage.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by iningosu on 10/9/2017.
 */

public class ApplianceModel {

    @DatabaseField(generatedId = true , columnName = "id")
    public int id;

    @DatabaseField
    public String deviceMacId;

    @DatabaseField
    public String deviceName;

    @DatabaseField
    public String relayNumber;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceMacId() {
        return deviceMacId;
    }

    public void setDeviceMacId(String deviceMacId) {
        this.deviceMacId = deviceMacId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRelayNumber() {
        return relayNumber;
    }

    public void setRelayNumber(String relayNumber) {
        this.relayNumber = relayNumber;
    }

    @Override
    public String toString() {
        return "ApplianceModel{" +
                "deviceName='" + deviceName + '\'' +
                ", relayNumber='" + relayNumber + '\'' +
                '}';
    }
}
