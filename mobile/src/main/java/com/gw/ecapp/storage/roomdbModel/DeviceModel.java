package com.gw.ecapp.storage.roomdbModel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by iningosu on 11/4/2017.
 */

@Parcel
@Entity (tableName = "deviceModel")
public class DeviceModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo
    public String deviceName;

    @ColumnInfo
    public String deviceSsid;

    @ColumnInfo
    public String devicePassword;

    @ColumnInfo
    public String modelNumber;

    @ColumnInfo
    public int channelCount;

    @ColumnInfo
    public int connMode;

    @ColumnInfo
    public String macId;
    @ColumnInfo
    public String mPreferredIP;

    @ColumnInfo
    public String mLastConnectedIP;

    // Name given by the end user
    @ColumnInfo
    public String configureName;

    @ColumnInfo
    public String applianceName;

    @ColumnInfo
    public String relayNumber;

  //  public ArrayList<ApplianceModel> connectedDevices;

    @ColumnInfo
    public String EXTRA_COL1;

    @ColumnInfo
    public String EXTRA_COL2;




}
