package com.ubb.bachelor.blebackgroundscan.domain.service;

import android.annotation.SuppressLint;
import android.content.Context;

import com.getcapacitor.PluginCall;
import com.ubb.bachelor.blebackgroundscan.R;
import com.ubb.bachelor.blebackgroundscan.data.repository.ScanResultRepository;
import com.ubb.bachelor.blebackgroundscan.domain.exception.ThreatServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.mapper.ScanResultExtendedToJSObject;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;
import com.ubb.bachelor.blebackgroundscan.domain.service.strategy.ConstantDeviceIdStrategy;
import com.ubb.bachelor.blebackgroundscan.domain.service.strategy.RotatingDeviceIdStrategy;
import com.ubb.bachelor.blebackgroundscan.domain.service.strategy.ThreatDetectionStrategy;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ThreatDetectionService {
    private static ThreatDetectionService instance;
    private Context context;
    private ScanResultRepository scanResultRepository;
    private NotificationService notificationService;

    private ThreatDetectionService(Context context, ScanResultRepository scanResultRepository) {
        this.context = context;
        this.scanResultRepository = scanResultRepository;
        this.notificationService = NotificationService.getInstance(context);
    }

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
        detectThreatForBeacons("AirTag", new RotatingDeviceIdStrategy(), 2);
        detectThreatForBeacons("SmartTag", new RotatingDeviceIdStrategy(), 3);
        detectThreatForBeacons("Tile", new ConstantDeviceIdStrategy(), 4);
    }

    @SuppressLint("CheckResult")
    private void detectThreatForBeacons(String deviceModel, ThreatDetectionStrategy strategy, int notificationId) {
        scanResultRepository.getScannedDevicesByModel(deviceModel)
                .subscribeOn(Schedulers.io())
                .subscribe((List<ScanResultExtended> scanResults) -> {
                    List<ScanResultExtended> possibleAirTagThreats = strategy.computeThreateningDevices(scanResults);
                    if(possibleAirTagThreats.size() > 0) {
                        this.notificationService.sendNotification(
                                notificationId,
                                notificationService.createNotification(
                                        deviceModel,
                                        "Possible " +  deviceModel + " device discovered in your possession",
                                        false,
                                        "2",
                                        R.drawable.threat_found
                                )
                        );
                    }
                });
    }


    @SuppressLint("CheckResult")
    public void getThreateningDevices(PluginCall call) {
        var rotatingIdStrategy = new RotatingDeviceIdStrategy();
        var constantIdStrategy = new ConstantDeviceIdStrategy();
        Observable.combineLatest(
                scanResultRepository.getScannedDevicesByModel("AirTag").toObservable(),
                scanResultRepository.getScannedDevicesByModel("SmartTag").toObservable(),
                scanResultRepository.getScannedDevicesByModel("Tile").toObservable(),
                (airTagDevices, smartTagDevices, tileDevices) -> {
                    List<ScanResultExtended> threats = new ArrayList<>();
                    threats.addAll(rotatingIdStrategy.computeThreateningDevices(airTagDevices));
                    threats.addAll(rotatingIdStrategy.computeThreateningDevices(smartTagDevices));
                    threats.addAll(constantIdStrategy.computeThreateningDevices(tileDevices));
                    return threats;
                }
        )
                .subscribeOn(Schedulers.io())
                .subscribe(possibleThreats -> call.resolve(ScanResultExtendedToJSObject.mapPossibleThreateningDeviceListToJSObject(possibleThreats)));
    }
}
