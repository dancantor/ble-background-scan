package com.ubb.bachelor.blebackgroundscan.domain.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ubb.bachelor.blebackgroundscan.domain.exception.DeviceScannerNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.exception.NotificationServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.exception.ThreatServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.service.DeviceScannerService;
import com.ubb.bachelor.blebackgroundscan.domain.service.NotificationService;
import com.ubb.bachelor.blebackgroundscan.domain.service.ThreatDetectionService;

public class ThreatDetectionWorker extends Worker {
    private ThreatDetectionService threatDetectionService;
    private Context context;

    public ThreatDetectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        try {
            threatDetectionService = ThreatDetectionService.getInstanceIfAvailable();
        } catch(ThreatServiceNotInstantiated exception) {
            Log.e("Worker", "ThreatDetectionService not initialized in worker");
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        if (threatDetectionService == null) {
            return Result.failure();
        }
        threatDetectionService.startThreatDetection();
        return Result.success();
    }
}
