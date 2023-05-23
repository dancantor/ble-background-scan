package com.ubb.bachelor.blebackgroundscan.domain.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.ubb.bachelor.blebackgroundscan.R;
import com.ubb.bachelor.blebackgroundscan.data.repository.ScanResultRepository;
import com.ubb.bachelor.blebackgroundscan.domain.exception.NotificationServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.exception.ThreatServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;
import com.ubb.bachelor.blebackgroundscan.domain.service.strategy.ConstantDeviceIdStrategy;
import com.ubb.bachelor.blebackgroundscan.domain.service.strategy.RotatingDeviceIdStrategy;
import com.ubb.bachelor.blebackgroundscan.domain.service.strategy.ThreatDetectionStrategy;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class ThreatDetectionService {
    private static ThreatDetectionService instance;
    private Context context;
    private ScanResultRepository scanResultRepository;
    private NotificationService notificationService;

    private ThreatDetectionService(Context context, ScanResultRepository scanResultRepository) {
        this.context = context;
        this.scanResultRepository = scanResultRepository;
        try {
            notificationService = NotificationService.getInstanceIfAvailable();
        } catch(NotificationServiceNotInstantiated exception) {
            Log.e("Worker", "NotificationService not initialized in worker");
        }    }

    public static ThreatDetectionService getInstance(Context context, ScanResultRepository scanResultRepository) {
        if (ThreatDetectionService.instance == null) {
            ThreatDetectionService.instance = new ThreatDetectionService(context, scanResultRepository);
        }
        return ThreatDetectionService.instance;
    }

    public static ThreatDetectionService getInstanceIfAvailable() throws ThreatServiceNotInstantiated {
        if (ThreatDetectionService.instance == null) {
            throw new ThreatServiceNotInstantiated("ThreatService was not instantiated");
        }
        return ThreatDetectionService.instance;
    }

    public void startThreatDetection() {
        detectThreatForBeacons("AirTag", new RotatingDeviceIdStrategy());
        detectThreatForBeacons("SmartTag", new RotatingDeviceIdStrategy());
        detectThreatForBeacons("Tile", new ConstantDeviceIdStrategy());
    }

    @SuppressLint("CheckResult")
    private void detectThreatForBeacons(String deviceModel, ThreatDetectionStrategy strategy) {
        scanResultRepository.getScannedDevicesByModel(deviceModel)
                .subscribeOn(Schedulers.io())
                .subscribe((List<ScanResultExtended> scanResults) -> {
                    List<ScanResultExtended> possibleAirTagThreats = strategy.computeThreateningDevices(scanResults);
                    if(possibleAirTagThreats.size() > 0) {
                        this.notificationService.sendNotification(
                                2,
                                notificationService.createNotification(
                                        "2",
                                        "Possible " +  deviceModel + " device discovered in your possession",
                                        false,
                                        "2",
                                        R.drawable.threat_found
                                )
                        );
                    }
                });
    }



}
