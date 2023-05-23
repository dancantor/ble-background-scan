package com.ubb.bachelor.blebackgroundscan.domain.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity(tableName = "Location")
public class LocationModel {
    @PrimaryKey
    @NonNull
    public UUID locationId;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "datetime")
    public LocalDateTime datetime;

    @ColumnInfo(name = "locationDeviceId")
    public String locationDeviceId;
}
