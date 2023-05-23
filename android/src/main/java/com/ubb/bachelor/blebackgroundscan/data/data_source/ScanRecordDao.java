package com.ubb.bachelor.blebackgroundscan.data.data_source;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ManufacturerData;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface ScanRecordDao {
    @Transaction
    @Query("SELECT * FROM ScanResultModel")
    public Single<List<ScanResultExtended>> getScanResults();

    @Transaction
    @Query("SELECT * FROM ScanResultModel WHERE deviceModel = :deviceModel")
    public Single<List<ScanResultExtended>> getScanResultsByModel(String deviceModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertScanResult(ScanResultModel scanRecord);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertManufacturerData(ManufacturerData manufacturerData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertLocation(LocationModel location);
}
