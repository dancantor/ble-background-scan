package com.ubb.bachelor.blebackgroundscan.domain.mapper;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.location.Location;
import android.os.Build;

import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ManufacturerData;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ScanResultMapper {
    @SuppressLint("MissingPermission")
    public static ScanResultExtended scanResultToScanResultModel(ScanResult scanResult, Location location) {
        ScanResultModel scanResultModel = new ScanResultModel();
        scanResultModel.deviceId = scanResult.getDevice().getAddress();
        scanResultModel.name = scanResult.getDevice().getName();
        scanResultModel.rssi = scanResult.getRssi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            scanResultModel.txPower = scanResult.getTxPower();
        }
        scanResultModel.rawAdvertisement = ByteMapper.byteArrayToString(scanResult.getScanRecord().getBytes());
        List<ManufacturerData> manufacturerDataList = new ArrayList<ManufacturerData>();
        var manufacturerSpecificData = scanResult.getScanRecord().getManufacturerSpecificData();
        if (manufacturerSpecificData != null) {
            for (int i = 0; i < manufacturerSpecificData.size(); ++i) {
                var key = manufacturerSpecificData.keyAt(i);
                var bytes = manufacturerSpecificData.get(key);
                ManufacturerData manufacturerData = new ManufacturerData();
                manufacturerData.id = UUID.randomUUID();
                manufacturerData.data = ByteMapper.byteArrayToString(bytes);
                manufacturerData.manufacturerDeviceId = scanResultModel.deviceId;
                manufacturerData.companyId = Integer.toString(key);
                manufacturerDataList.add(manufacturerData);
            }
        }
        ScanResultExtended scanResultWithManufacturerData = new ScanResultExtended();
        scanResultWithManufacturerData.scanResult = scanResultModel;
        scanResultWithManufacturerData.manufacturerData = manufacturerDataList;
        if (location == null) {
            scanResultWithManufacturerData.location = new ArrayList<LocationModel>();
            return scanResultWithManufacturerData;
        }
        scanResultWithManufacturerData.location = List.of(LocationMapper.locationToLocationModel(location, scanResultModel.deviceId));
        return scanResultWithManufacturerData;
    }
}
