package com.ubb.bachelor.blebackgroundscan;

import static android.provider.Settings.ACTION_BLUETOOTH_SETTINGS;
import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

import android.Manifest;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.bluetooth.*;
import android.provider.Settings.*;
import java.util.*;
import android.content.pm.PackageManager;




import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.ubb.bachelor.blebackgroundscan.data.repository.ScanResultRepository;
import com.ubb.bachelor.blebackgroundscan.domain.service.DeviceScannerService;
import com.ubb.bachelor.blebackgroundscan.domain.service.NotificationService;
import com.ubb.bachelor.blebackgroundscan.domain.service.ThreatDetectionService;

import android.content.pm.PackageManager;
import android.content.Context;
import android.util.Log;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CapacitorPlugin(
        name = "BleBackgroundScan",
        permissions = {
                @Permission(
                      strings = { Manifest.permission.ACCESS_COARSE_LOCATION },
                        alias = "ACCESS_COARSE_LOCATION"
                ),
                @Permission(
                        strings = { Manifest.permission.ACCESS_FINE_LOCATION },
                        alias = "ACCESS_FINE_LOCATION"
                ),
                @Permission(
                        strings = { Manifest.permission.BLUETOOTH },
                        alias = "BLUETOOTH"
                ),
                @Permission(
                        strings = { Manifest.permission.BLUETOOTH_ADMIN },
                        alias = "BLUETOOTH_ADMIN"
                ),
                @Permission(
                        strings = { Manifest.permission.BLUETOOTH_SCAN },
                        alias = "BLUETOOTH_SCAN"
                ),
                @Permission(
                        strings = { Manifest.permission.BLUETOOTH_CONNECT },
                        alias = "BLUETOOTH_CONNECT"
                ),
                @Permission(
                        strings = { Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        alias = "ACCESS_BACKGROUND_LOCATION"
                ),
                @Permission(
                        strings = { Manifest.permission.POST_NOTIFICATIONS},
                        alias = "POST_NOTIFICATIONS"
                )
        }
)
public class BleBackgroundScanPlugin extends Plugin {
    private BleBackgroundScan implementation = new BleBackgroundScan();
    private BluetoothAdapter bluetoothAdapter = null;
    private List<String> aliases = new ArrayList<>();
    private ScanResultRepository scanResultRepository;
    private DeviceScannerService deviceScannerService;
    private ThreatDetectionService threatDetectionService;
    private NotificationService notificationService;

    @PluginMethod()
    public void initiateBackgroundScan(PluginCall call) {
        if (!assertBluetoothAdapter(call)) {
            return;
        }
        if (this.deviceScannerService != null) {
            Log.i("plugin", "isScanning");
            implementation.initiateBackgroundScan(getContext());
        }
        else {
            Log.i("plugin_pl", "deviceScanner no init");
        }
        call.resolve();
    }

    @PluginMethod()
    public void initiateThreatDetection(PluginCall call) {
        if (this.threatDetectionService != null) {
            Log.i("plugin", "threatDetection");
            implementation.initiateThreatDetection(getContext());
        }
        else {
            Log.i("plugin_pl", "deviceScanner no init");
        }
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    public void initialize(PluginCall call) {
        if (Build.VERSION.SDK_INT >= 31) {
            aliases = Arrays.asList(
                        "BLUETOOTH_SCAN",
                        "BLUETOOTH_CONNECT",
                        "ACCESS_FINE_LOCATION",
                        "POST_NOTIFICATIONS"
                        );
        } else {
            aliases = Arrays.asList(
                    "ACCESS_COARSE_LOCATION",
                    "ACCESS_FINE_LOCATION",
                    "BLUETOOTH",
                    "BLUETOOTH_ADMIN"
                    );
        }
        requestPermissionForAliases(aliases.toArray(new String[0]), call, "checkPermission");
    }


    @PermissionCallback
    private void checkPermission(PluginCall call) {
        List<Boolean> grantedPermissions = aliases.stream().map((String alias) -> {
            return getPermissionState(alias) == PermissionState.GRANTED;
        }).collect(Collectors.toList());
        if (grantedPermissions.stream().allMatch((Boolean permission) -> permission)) {
            runInitialization(call);
        }
        else {
            call.reject("Permission denied.");
        }
    }

    private void runInitialization(PluginCall call) {
        var packageManager = getContext().getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            call.reject("BLE is not supported.");
            return;
        }

        bluetoothAdapter =
                ((BluetoothManager)getContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        this.scanResultRepository = ScanResultRepository.getInstance(getContext());
        this.notificationService = NotificationService.getInstance(getContext());
        this.deviceScannerService = DeviceScannerService.getInstance(getContext(), bluetoothAdapter, this.scanResultRepository);
        this.threatDetectionService = ThreatDetectionService.getInstance(getContext(), scanResultRepository);
        notificationService.createChannel(
                "Scanning progress Notification",
                "Notifications for informing about scanning",
                NotificationManager.IMPORTANCE_DEFAULT,
                "1"
                );
        notificationService.createChannel(
                "Threatening device Notification",
                "Notifications for informing about possible malicious beacons",
                NotificationManager.IMPORTANCE_DEFAULT,
                "2"
        );
        if (bluetoothAdapter == null) {
            call.reject("BLE is not available.");
            return;
        }
        call.resolve();
    }

    private Boolean assertBluetoothAdapter(PluginCall call) {
        if (bluetoothAdapter == null) {
            call.reject("Bluetooth LE not initialized.");
            return false;
        }
        return true;
    }
}
