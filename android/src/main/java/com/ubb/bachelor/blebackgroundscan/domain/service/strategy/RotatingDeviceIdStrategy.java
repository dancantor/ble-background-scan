package com.ubb.bachelor.blebackgroundscan.domain.service.strategy;

import android.util.Log;

import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultModel;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RotatingDeviceIdStrategy extends ThreatDetectionStrategy{
    @Override
    public List<ScanResultExtended> computeThreateningDevices(List<ScanResultExtended> allScanResults) {
//        Log.i("RotatingDetection", "Scanning for AirTag/SmartTag");
        if (allScanResults.size() == 0)
            return new ArrayList<>();
        ScanResultExtended deviceWithAllLocations = allScanResults.stream()
                .reduce(getNoLocationCopy(allScanResults.get(0)),
                        (accumulatedDevices, scanResult) -> {
                                accumulatedDevices.location.addAll(scanResult.location);
                                return accumulatedDevices;
                            }
                        );
        deviceWithAllLocations = removeLocationDuplicates(deviceWithAllLocations);
        List<ScanResultExtended> devicesGropedByConsecutiveIntervals = groupPossibleDevicesByConsecutiveOccurrences(deviceWithAllLocations);
        return devicesGropedByConsecutiveIntervals.stream()
                .filter(
                        scanResultExtended -> getMaximumTimeRange(List.of(scanResultExtended)) >= 30 &&
                                getMaximumDistance(List.of(scanResultExtended)) >= 0.5)
                .collect(Collectors.toList());
    }

    private ScanResultExtended getNoLocationCopy(ScanResultExtended scanResultExtended) {
        ScanResultExtended scanResult = new ScanResultExtended();
        scanResult.scanResult = scanResultExtended.scanResult;
        scanResult.manufacturerData = scanResultExtended.manufacturerData;
        scanResult.location = new ArrayList<LocationModel>();
        return scanResult;
    }

    public ScanResultExtended removeLocationDuplicates(ScanResultExtended scanResultExtended) {
        scanResultExtended.location.sort(Comparator.comparing(location -> location.datetime));
        List<LocationModel> filteredLocations = new ArrayList<LocationModel>();
        for (int i = 0; i < scanResultExtended.location.size(); ++i) {
            var currentLocation = scanResultExtended.location.get(i);
            if (shouldIncludeScanLocation(filteredLocations, currentLocation)) {
                filteredLocations.add(currentLocation);
            }
        }
        scanResultExtended.location = filteredLocations;
        return scanResultExtended;
    }

    private boolean shouldIncludeScanLocation(List<LocationModel> locations, LocationModel location) {
        return locations
                .stream()
                .noneMatch(
                        locationModel ->
                                computeIntervalBetweenTwoDates(
                                        location.datetime, locationModel.datetime, ChronoUnit.SECONDS) < 15 &&
                                        location.locationDeviceId.equals(locationModel.locationDeviceId)
                );
    }

    public List<ScanResultExtended> groupPossibleDevicesByConsecutiveOccurrences(ScanResultExtended scanResultWithAllLocations) {
        List<ScanResultExtended> result = new ArrayList<ScanResultExtended>();
        for (int i = 0; i < scanResultWithAllLocations.location.size(); ++i) {
            var currentLocation = scanResultWithAllLocations.location.get(i);
            boolean foundMatchingExistingDevice = false;
            for (int j = 0; j < result.size() && !foundMatchingExistingDevice; ++j) {
                var currentDevice = result.get(j);
                if (currentDevice.scanResult.deviceId.equals(currentLocation.locationDeviceId)) {
                    var newLocations = new ArrayList<>(currentDevice.location);
                    newLocations.add(currentLocation);
                    currentDevice.location = newLocations;
                    foundMatchingExistingDevice = true;
                    continue;
                }
                var lastLocation = currentDevice.location.get(currentDevice.location.size() - 1);
                long timeIntervalBetweenLocationObjects = computeIntervalBetweenTwoDates(lastLocation.datetime, currentLocation.datetime, ChronoUnit.MINUTES);
                if (timeIntervalBetweenLocationObjects >= 14 && timeIntervalBetweenLocationObjects <= 16) {
                    var newLocations = new ArrayList<>(currentDevice.location);
                    newLocations.add(currentLocation);
                    currentDevice.location = newLocations;
                    foundMatchingExistingDevice = true;
                }
            }
            if (foundMatchingExistingDevice) {
                continue;
            }
            ScanResultExtended newDiscoveredDevice = new ScanResultExtended();
            newDiscoveredDevice.scanResult = new ScanResultModel();
            newDiscoveredDevice.scanResult.deviceId = currentLocation.locationDeviceId;
            newDiscoveredDevice.scanResult.deviceModel = scanResultWithAllLocations.scanResult.deviceModel;
            newDiscoveredDevice.location = List.of(currentLocation);
            result.add(newDiscoveredDevice);
        }
        return result;
    }
}
