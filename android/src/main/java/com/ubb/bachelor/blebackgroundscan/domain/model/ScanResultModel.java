package com.ubb.bachelor.blebackgroundscan.domain.model;

import android.bluetooth.le.ScanResult;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName = "ScanResultModel")
public class ScanResultModel {
    @PrimaryKey
    @NonNull
    public String deviceId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "rssi")
    public Integer rssi;

    @ColumnInfo(name = "txPower")
    public Integer txPower;

    @ColumnInfo(name = "rawAdvertisement")
    public String rawAdvertisement;

    @ColumnInfo(name = "deviceModel")
    public String deviceModel;
}

