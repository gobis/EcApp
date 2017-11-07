package com.gw.ecapp.storage.roomdbModel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

/**
 * Created by iningosu on 11/4/2017.
 */

@Parcel
@Entity(tableName = "applianceModel", foreignKeys = @ForeignKey(entity = DeviceModel.class,
        parentColumns = "id",
        childColumns = "deviceId"))
public class ApplianceModel {

    @PrimaryKey(autoGenerate = true )
    public int id;

    @ColumnInfo(name = "deviceId")
    public String deviceId;

    @ColumnInfo
    public String deviceMacId;

    @ColumnInfo
    public String deviceName;

    @ColumnInfo
    public String relayNumber;





}
