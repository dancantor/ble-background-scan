package com.ubb.bachelor.blebackgroundscan.domain.workers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ubb.bachelor.blebackgroundscan.R;
import com.ubb.bachelor.blebackgroundscan.data.repository.ScanResultRepository;
import com.ubb.bachelor.blebackgroundscan.domain.exception.DeviceScannerNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.exception.NotificationServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.service.DeviceScannerService;
import com.ubb.bachelor.blebackgroundscan.domain.service.NotificationService;

public class BleScanningWorker extends Worker {
    private DeviceScannerService deviceScannerService;
    private NotificationService notificationService;
    private BluetoothAdapter bluetoothAdapter;
    private ScanResultRepository scanResultRepository;
    private Context context;

    public BleScanningWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        bluetoothAdapter =
                ((BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        scanResultRepository = ScanResultRepository.getInstance(context);
        deviceScannerService = DeviceScannerService.getInstance(context, bluetoothAdapter, scanResultRepository);
        notificationService = NotificationService.getInstance(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (deviceScannerService == null) {
            return Result.failure();
        }
        Log.i("Worker", "startScanning");
        notificationService.sendNotification(
                1,
                notificationService.createNotification("1", "Scanning for devices", false, "1", R.drawable.bluetooth)
        );
        deviceScannerService.startScanning();
        return Result.success();
    }

}
