package com.ubb.bachelor.blebackgroundscan.domain.mapper;

import android.location.Location;

import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class LocationMapper {
    public static LocationModel locationToLocationModel(Location location, String deviceId) {
        LocationModel locationModel = new LocationModel();
        locationModel.locationId = UUID.randomUUID();
        locationModel.datetime = LocalDateTime.now();
        locationModel.latitude = location.getLatitude();
        locationModel.longitude = location.getLongitude();
        locationModel.locationDeviceId = deviceId;
        return locationModel;
    }
}
