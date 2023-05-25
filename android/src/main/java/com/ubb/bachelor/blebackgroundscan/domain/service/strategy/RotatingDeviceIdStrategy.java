package com.ubb.bachelor.blebackgroundscan.domain.service.strategy;

import android.util.Log;

import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class RotatingDeviceIdStrategy extends ThreatDetectionStrategy{
    @Override
    public List<ScanResultExtended> computeThreateningDevices(List<ScanResultExtended> allScanResults) {
        Log.i("RotatingDetection", "Scanning for AirTag/SmartTag");
        if (allScanResults.size() == 0)
            return new ArrayList<>();
        ScanResultExtended deviceWithAllLocations = allScanResults.stream()
                .reduce(getNoLocationCopy(allScanResults.get(0)),
                        (accumulatedDevices, scanResult) -> {
                                accumulatedDevices.location.addAll(scanResult.location);
                                return accumulatedDevices;
                            }
                        );
        if (getMaximumDistance(List.of(deviceWithAllLocations)) >= 0.1 && getMaximumTimeRange(List.of(deviceWithAllLocations)) >= 1) {
            return allScanResults;
        }
        return new ArrayList<>();
    }

    private ScanResultExtended getNoLocationCopy(ScanResultExtended scanResultExtended) {
        ScanResultExtended scanResult = new ScanResultExtended();
        scanResult.scanResult = scanResultExtended.scanResult;
        scanResult.manufacturerData = scanResultExtended.manufacturerData;
        scanResult.location = new ArrayList<LocationModel>();
        return scanResult;
    }
}
