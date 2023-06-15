package com.ubb.bachelor.blebackgroundscan.domain.service;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.ubb.bachelor.blebackgroundscan.data.repository.ScanResultRepository;
import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.stream.Collectors;

import io.reactivex.schedulers.Schedulers;

public class DataPurgingService {
    private static DataPurgingService instance;
    private ScanResultRepository scanResultRepository;

    private DataPurgingService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    public static DataPurgingService getInstance(ScanResultRepository scanResultRepository) {
        if (DataPurgingService.instance == null) {
            DataPurgingService.instance = new DataPurgingService(scanResultRepository);
        }
        return DataPurgingService.instance;
    }

    @SuppressLint("CheckResult")
    public void executeDataPurging() {
        scanResultRepository.getScanResults()
                .subscribeOn(Schedulers.io())
                .subscribe(scanResults -> {
                    for (ScanResultExtended scanResult : scanResults) {
                        scanResult.location = scanResult.location
                                .stream()
                                .peek(location -> {
                                    if (computeIntervalBetweenTwoDates(LocalDateTime.now(), location.datetime, ChronoUnit.HOURS) >= 6)
                                    {
                                        scanResultRepository.deleteLocation(location)
                                                .subscribeOn(Schedulers.io())
                                                .subscribe();
                                    }
                                })
                                .filter(location -> computeIntervalBetweenTwoDates(LocalDateTime.now(), location.datetime, ChronoUnit.HOURS) < 6)
                                .collect(Collectors.toList());
                        if (scanResult.location.size() == 0) {
                            scanResultRepository.deleteManufacturerDataByDeviceId(scanResult.scanResult.deviceId)
                                    .andThen(scanResultRepository.deleteScanResult(scanResult.scanResult))
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(() -> Log.i("DataPurging", "ScanResult Successfully Removed"));

                        }
                    }
                });
    }

    private long computeIntervalBetweenTwoDates(LocalDateTime date1, LocalDateTime date2, ChronoUnit unit) {
        return Math.abs(unit.between(date1, date2));
    }
}
