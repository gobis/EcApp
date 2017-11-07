package com.gw.ecapp.storage.roomdbModel;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Created by iningosu on 11/4/2017.
 */

public class DeviceWithAppliance {

    @Embedded
    public DeviceModel mDevice;

    @Relation(parentColumn = "id", entityColumn = "deviceId", entity = ApplianceModel.class)
    public List<ApplianceModel> mAppliance; //
    /*
    parentColumn refers to Embedded DeviceModel table's id column,
    entityColumn refers to ApplianceModel table's deviceId (User - Pet relation) column,
    entity refers to table(ApplianceModel) which has relation with DeviceModel table.
    */

}
