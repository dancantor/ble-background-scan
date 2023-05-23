package com.ubb.bachelor.blebackgroundscan;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ubb.bachelor.blebackgroundscan.domain.workers.BleScanningWorker;
import com.ubb.bachelor.blebackgroundscan.domain.workers.ThreatDetectionWorker;

import java.util.concurrent.TimeUnit;

public class BleBackgroundScan {
    public void initiateBackgroundScan(Context context) {
        PeriodicWorkRequest echoRequest = new PeriodicWorkRequest.Builder(BleScanningWorker.class, 15, TimeUnit.MINUTES)
                .addTag("backgroundScan")
                .build();
        WorkManager.getInstance(context).enqueue(echoRequest);
    }

    public void initiateThreatDetection(Context context) {
        PeriodicWorkRequest echoRequest = new PeriodicWorkRequest.Builder(ThreatDetectionWorker.class, 15, TimeUnit.MINUTES)
                .addTag("threatDetection")
//                .setInitialDelay(1, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(context).enqueue(echoRequest);
    }

}
