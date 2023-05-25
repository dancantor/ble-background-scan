package com.ubb.bachelor.blebackgroundscan.domain.service.strategy;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;

import java.util.List;
import java.util.stream.Collectors;

public class ConstantDeviceIdStrategy extends ThreatDetectionStrategy{
    @Override
    public List<ScanResultExtended> computeThreateningDevices(List<ScanResultExtended> allScanResults) {
        Log.i("ConstantDetection", "Scanning for Tiles");
        return allScanResults.stream()
                .filter(
                        scanResultExtended -> getMaximumTimeRange(List.of(scanResultExtended)) >= 1 &&
                                getMaximumDistance(List.of(scanResultExtended)) >= 0.1)
                .collect(Collectors.toList());
    }
}
