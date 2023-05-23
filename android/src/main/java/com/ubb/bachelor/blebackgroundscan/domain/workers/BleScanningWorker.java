package com.ubb.bachelor.blebackgroundscan.domain.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ubb.bachelor.blebackgroundscan.R;
import com.ubb.bachelor.blebackgroundscan.domain.exception.DeviceScannerNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.exception.NotificationServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.service.DeviceScannerService;
import com.ubb.bachelor.blebackgroundscan.domain.service.NotificationService;

public class BleScanningWorker extends Worker {
    private DeviceScannerService deviceScannerService;
    private NotificationService notificationService;
    private Context context;

    public BleScanningWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        try {
        deviceScannerService = DeviceScannerService.getInstanceIfAvailable();
        notificationService = NotificationService.getInstanceIfAvailable();
        } catch(DeviceScannerNotInstantiated exception) {
            Log.e("Worker", "DeviceScannerService not initialized in worker");
        }
        catch(NotificationServiceNotInstantiated exception) {
            Log.e("Worker", "NotificationService not initialized in worker");
        }
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
                notificationService.createNotification("1", "Scanning for devices", true, "1", R.drawable.bluetooth)
        );
        deviceScannerService.startScanning();
        return Result.success();
    }

}
