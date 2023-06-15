package com.ubb.bachelor.blebackgroundscan.domain.service;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.ubb.bachelor.blebackgroundscan.R;
import com.ubb.bachelor.blebackgroundscan.data.dto.BlacklistForDevices;
import com.ubb.bachelor.blebackgroundscan.data.repository.ScanResultRepository;
import com.ubb.bachelor.blebackgroundscan.domain.callbacks.LocationResultCallback;
import com.ubb.bachelor.blebackgroundscan.domain.exception.DeviceScannerNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.exception.InvalidHexadecimalCharacter;
import com.ubb.bachelor.blebackgroundscan.domain.exception.NotificationServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.mapper.ByteMapper;
import com.ubb.bachelor.blebackgroundscan.domain.mapper.ScanResultMapper;
import com.ubb.bachelor.blebackgroundscan.domain.model.BlacklistForAirTagAndSmartTag;
import com.ubb.bachelor.blebackgroundscan.domain.model.BlacklistForTiles;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;

import org.apache.commons.lang3.ArrayUtils;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class DeviceScannerService {
    private static DeviceScannerService instance;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanResultRepository scanResultRepository;
    private NotificationService notificationService;
    private Set<String> discoveredAirTags;
    private Set<String> discoveredSmartTags;
    private Boolean isScanning = false;
    private final String SCANNING_TAG = DeviceScannerService.class.getSimpleName();
    private long MAX_AGE_SECONDS = 100L;
    private long MIN_ACCURACY_METER = 100L;

    private DeviceScannerService(Context context, BluetoothAdapter bluetoothAdapter,
                                 ScanResultRepository scanResultRepository) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.scanResultRepository = scanResultRepository;
        this.notificationService = NotificationService.getInstance(context);
        this.discoveredAirTags = new HashSet<>();
        this.discoveredSmartTags = new HashSet<>();
    }

    public static DeviceScannerService getInstance(
            Context context,
            BluetoothAdapter bluetoothAdapter,
            ScanResultRepository scanResultRepository
    ) {
        if (DeviceScannerService.instance == null) {
            DeviceScannerService.instance = new DeviceScannerService(context, bluetoothAdapter, scanResultRepository);
        }
        return DeviceScannerService.instance;
    }

    public static DeviceScannerService getInstanceIfAvailable() throws DeviceScannerNotInstantiated {
        if (DeviceScannerService.instance == null) {
            throw new DeviceScannerNotInstantiated("DeviceScannerService was not instantiated");
        }
        return DeviceScannerService.instance;
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (!isTrackingDevice(result)) return;
            getCurrentLocation(new LocationResultCallback() {
                @Override
                public void success(Location location) {
                    addScanResultToDatabase(location, result);
                }

                @Override
                public void error(String message) {
                    Log.e("LocationError", message);
                    addScanResultToDatabase(null, result);
                }
            });
        }
    };

    private boolean isTrackingDevice(ScanResult result) {
        var scanResult = ScanResultMapper.scanResultToScanResultModel(result, null);
        return (isAirTagDevice(scanResult) || isTileDevice(result) || isSmartTagDevice(result));
    }

    private boolean isAirTagDevice(ScanResultExtended scanResult) {
        return scanResult.manufacturerData.stream().anyMatch(
                (manufacturerData) -> manufacturerData.companyId.equals("76")) &&
                scanResult.scanResult.rawAdvertisement.substring(2, 4).equals("FF") &&
                scanResult.scanResult.rawAdvertisement.substring(8, 10).equals("12") &&
                scanResult.manufacturerData.stream().anyMatch(
                        (manufacturerData -> isStatusByteOfAirTag(manufacturerData.data))
                );
    }

    private boolean isTileDevice(ScanResult scanResult) {
        return scanResult.getScanRecord().getServiceData().keySet().stream().anyMatch(
                (serviceUuid) ->
                        serviceUuid.toString().contains("feed"));
    }

    private boolean isSmartTagDevice(ScanResult scanResult) {
        return scanResult.getScanRecord().getServiceData().keySet().stream().anyMatch(
                (serviceUuid) ->
                        serviceUuid.toString().contains("fd59") ||
                                serviceUuid.toString().contains("fd5a")
        );
    }

    @SuppressLint({"CheckResult"})
    private void addScanResultToDatabase(Location location, ScanResult result) {
        var scanResult = ScanResultMapper.scanResultToScanResultModel(result, location);
//        if(computeDistanceToDevice(scanResult) > 2) {
//            Log.i("Distance", computeDistanceToDevice(scanResult).toString());
//            return;
//        }
        if (isAirTagDevice(scanResult)) {
            scanResult.scanResult.deviceModel = "AirTag";
            scanResultRepository.getBlacklistForAirTagAndSmartTag()
                    .flatMapCompletable(blacklist -> {
                        if (discoveredAirTags.size() < blacklist.ignoredAirTags) {
                            discoveredAirTags.add(scanResult.scanResult.deviceId);
                            return Completable.complete();
                        }
                        else if (!discoveredAirTags.contains(scanResult.scanResult.deviceId)) {
                            return scanResultRepository.insertScanResult(scanResult);
                        }
                        return Completable.complete();
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> Log.i("Apple", scanResult.scanResult.deviceId));
        }
        if (isTileDevice(result)) {
            scanResult.scanResult.deviceModel = "Tile";
            scanResultRepository.isScanResultInTileBlacklist(scanResult)
                    .flatMapCompletable(isInBlacklist -> {
                        if (isInBlacklist) {
                            return Completable.complete();
                        }
                        return scanResultRepository.insertScanResult(scanResult);
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> Log.i("Tile", scanResult.scanResult.deviceId));
        }
        if (isSmartTagDevice(result)) {
            scanResult.scanResult.deviceModel = "SmartTag";
            scanResultRepository.getBlacklistForAirTagAndSmartTag()
                    .flatMapCompletable(blacklist -> {
                        if (discoveredSmartTags.size() < blacklist.ignoredSmartTags) {
                            discoveredSmartTags.add(scanResult.scanResult.deviceId);
                            return Completable.complete();
                        }
                        else if (!discoveredSmartTags.contains(scanResult.scanResult.deviceId)) {
                            return scanResultRepository.insertScanResult(scanResult);
                        }
                        return Completable.complete();
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> Log.i("SmartTag", scanResult.scanResult.deviceId));
        }
    }

    public void startScanning() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            this.notificationService.sendNotification(
                    1,
                    this.notificationService.createNotification(
                            "no_bluetooth",
                            "Bluetooth not enabled, so background scan is not available",
                            false,
                            "1",
                            R.drawable.no_bluetooth
                    )
            );
            return;
        }
        if (!isScanning) {
            setTimeoutForStopScanning();
            Logger.debug("SCANNING_TAG", "Started Scanning");
            isScanning = true;
            bluetoothLeScanner.startScan(scanCallback);
        }
    }

    @SuppressLint("MissingPermission")
    public void stopScanning() {
        isScanning = false;
        this.notificationService.removeNotification(1);
        bluetoothLeScanner.stopScan(scanCallback);
        discoveredAirTags = new HashSet<>();
        discoveredSmartTags = new HashSet<>();
    }

    private boolean isStatusByteOfAirTag(String data) {
        if (data.length() < 6) return false;
        try {
            var statusByte = ArrayUtils.toObject(ByteMapper.stringToByteArray(data.substring(4, 6)))[0];
            int deviceType = (statusByte & (0x30)) >> 4;
            return deviceType == 1;
        } catch (InvalidHexadecimalCharacter exception) {
            Log.i("Plugin", "Invalid hex");
            return false;
        }
    }

    private void setTimeoutForStopScanning() {
        var stopScanHandler = new Handler(Looper.getMainLooper());
        stopScanHandler.postDelayed(this::stopScanning, 15000);
    }

    private void getCurrentLocation(LocationResultCallback resultCallback) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (this.isLocationServicesEnabled()) {
                boolean networkEnabled = false;

                try {
                    networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                }

                int lowPriority = networkEnabled ? Priority.PRIORITY_BALANCED_POWER_ACCURACY : Priority.PRIORITY_LOW_POWER;

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    resultCallback.error("Location permission unavailable");
                    return;
                }
                LocationServices
                        .getFusedLocationProviderClient(context)
                        .getLastLocation()
                        .addOnFailureListener(e -> resultCallback.error(e.getMessage()))
                        .addOnSuccessListener(
                                location -> {
                                    if (location != null && locationMatchesMinimumRequirements(location)) {
                                        resultCallback.success(location);
                                    } else {
                                        resultCallback.error("Location unavailable");
                                    }
                                }
                        );
            } else {
                resultCallback.error("location disabled");
            }
        } else {
            resultCallback.error("Google Play Services not available");
        }
    }


    private boolean locationMatchesMinimumRequirements(Location location) {
        return location.getAccuracy() <= MIN_ACCURACY_METER && getSecondsSinceLocation(location) <= MAX_AGE_SECONDS;
    }
    private long getSecondsSinceLocation(Location location) {
        var millisecondsSinceLocation = (SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos()) / 1000000L;
        var timeOfLocationEvent = System.currentTimeMillis() - millisecondsSinceLocation;
        var locationDate = Instant.ofEpochMilli(timeOfLocationEvent).atZone(ZoneId.systemDefault()).toLocalDateTime();
        var timeDiff = ChronoUnit.SECONDS.between(locationDate, LocalDateTime.now());

        return timeDiff;
    }

    public Boolean isLocationServicesEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(lm);
    }

    private Double computeDistanceToDevice(ScanResultExtended scanResult) {
        if (scanResult.scanResult.txPower == null || scanResult.scanResult.txPower == 127) {
            scanResult.scanResult.txPower = -69;
        }
        return Math.pow(10F, (scanResult.scanResult.txPower - scanResult.scanResult.rssi) / (10.0 * 2));
    }

    @SuppressLint("CheckResult")
    public void setBlacklistForDevices(BlacklistForDevices blacklistForDevices, PluginCall call) {
        var blacklistForTiles = blacklistForDevices.tilesID
                .stream()
                .map(tileId -> {
                    BlacklistForTiles blacklistForTile = new BlacklistForTiles();
                    blacklistForTile.tileId = tileId;
                    return blacklistForTile;
                }).collect(Collectors.toList());
        scanResultRepository.deleteAllTilesFromBlacklist()
                .andThen(scanResultRepository.bulkInsertBlacklistForTiles(blacklistForTiles))
                .subscribeOn(Schedulers.io())
                .subscribe(() -> Log.i("TileBlacklist", "Tiles successfully added to blacklist"));
        var blacklistForAirTagAndSmartTag = new BlacklistForAirTagAndSmartTag();
        blacklistForAirTagAndSmartTag.id = 1;
        blacklistForAirTagAndSmartTag.ignoredAirTags = blacklistForDevices.airTagThreshold;
        blacklistForAirTagAndSmartTag.ignoredSmartTags = blacklistForDevices.smartTagThreshold;
        scanResultRepository.updateBlacklistForAirTagAndSmartTag(blacklistForAirTagAndSmartTag)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    Log.i("AirTag&SmartTag_Blacklist", "AirTags and SmartTags successfully added to blacklist");
                    call.resolve();
                });

    }

    public void initializeBlacklistForAirTagAndSmartTag() {
        var blacklistForAirTagAndSmartTag = new BlacklistForAirTagAndSmartTag();
        blacklistForAirTagAndSmartTag.id = 1;
        blacklistForAirTagAndSmartTag.ignoredAirTags = 0;
        blacklistForAirTagAndSmartTag.ignoredSmartTags = 0;
        scanResultRepository.insertBlacklistForAirTagAndSmartTag(blacklistForAirTagAndSmartTag)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> Log.i("AirTag&SmartTag_Blacklist", "AirTags and SmartTags successfully added to blacklist"));
    }
}
