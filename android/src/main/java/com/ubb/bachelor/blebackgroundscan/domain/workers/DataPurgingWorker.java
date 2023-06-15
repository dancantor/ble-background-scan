package com.ubb.bachelor.blebackgroundscan.domain.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ubb.bachelor.blebackgroundscan.data.repository.ScanResultRepository;
import com.ubb.bachelor.blebackgroundscan.domain.service.DataPurgingService;

public class DataPurgingWorker extends Worker {
    private DataPurgingService dataPurgingService;

    public DataPurgingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        var scanResultRepository = ScanResultRepository.getInstance(context);
        dataPurgingService = DataPurgingService.getInstance(scanResultRepository);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (dataPurgingService == null) {
            return Result.failure();
        }
        dataPurgingService.executeDataPurging();
        return Result.success();
    }
}
