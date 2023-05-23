package com.ubb.bachelor.blebackgroundscan.domain.model;

import android.location.Location;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ScanResultExtended {
    @Embedded
    public ScanResultModel scanResult;
    @Relation(
            parentColumn = "deviceId",
            entityColumn = "manufacturerDeviceId"
    )
    public List<ManufacturerData> manufacturerData;
    @Relation(
            parentColumn = "deviceId",
            entityColumn = "locationDeviceId"
    )
    public List<LocationModel> location;
}
