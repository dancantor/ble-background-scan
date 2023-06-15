package com.ubb.bachelor.blebackgroundscan.data.dto;

import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;

import java.util.List;

public class ThreateningTracker {
    List<String> deviceIds;
    List<LocationModel> locations;
    String deviceType;
}
