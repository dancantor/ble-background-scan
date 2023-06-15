package com.ubb.bachelor.blebackgroundscan;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.work.OutOfQuotaPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ubb.bachelor.blebackgroundscan.domain.service.DataPurgingService;
import com.ubb.bachelor.blebackgroundscan.domain.workers.BleScanningWorker;
import com.ubb.bachelor.blebackgroundscan.domain.workers.DataPurgingWorker;
import com.ubb.bachelor.blebackgroundscan.domain.workers.ThreatDetectionWorker;

import java.util.concurrent.TimeUnit;

public class BleBackgroundScan {
    public void initiateBackgroundScan(Context context) {
        PeriodicWorkRequest backgroundScanRequest = new PeriodicWorkRequest.Builder(BleScanningWorker.class, 15, TimeUnit.MINUTES)
                .addTag("backgroundScan")
                .build();
        WorkManager.getInstance(context).enqueue(backgroundScanRequest);
    }

    public void initiateThreatDetection(Context context) {
        PeriodicWorkRequest threatDetectionRequest = new PeriodicWorkRequest.Builder(ThreatDetectionWorker.class, 16, TimeUnit.MINUTES)
                .addTag("threatDetection")
                .build();
        WorkManager.getInstance(context).enqueue(threatDetectionRequest);
    }

    public void initiatePeriodicDataPurging(Context context) {
        PeriodicWorkRequest dataPurgingRequest = new PeriodicWorkRequest.Builder(DataPurgingWorker.class, 6, TimeUnit.HOURS)
                .addTag("dataPurging")
                .build();
        WorkManager.getInstance(context).enqueue(dataPurgingRequest);
    }

}
