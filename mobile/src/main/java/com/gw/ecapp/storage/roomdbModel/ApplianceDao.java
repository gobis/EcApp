package com.gw.ecapp.storage.roomdbModel;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by iningosu on 11/4/2017.
 */

@Dao
public interface ApplianceDao {

    @Query("Select * from applianceModel")
    List<ApplianceModel> getAllApplianceModel();

}
