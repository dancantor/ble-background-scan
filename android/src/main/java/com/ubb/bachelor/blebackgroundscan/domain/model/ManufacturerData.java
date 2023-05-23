package com.ubb.bachelor.blebackgroundscan.domain.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "ManufacturerData")
public class ManufacturerData {
    @PrimaryKey
    @NonNull
    public UUID id;

    @ColumnInfo(name = "companyId")
    public String companyId;

    @ColumnInfo(name = "data")
    public String data;

    @ColumnInfo(name = "manufacturerDeviceId")
    public String manufacturerDeviceId;
}
