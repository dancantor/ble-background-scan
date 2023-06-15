package com.ubb.bachelor.blebackgroundscan.data.data_source;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.ubb.bachelor.blebackgroundscan.domain.model.BlacklistForAirTagAndSmartTag;
import com.ubb.bachelor.blebackgroundscan.domain.model.BlacklistForTiles;
import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ManufacturerData;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultModel;

import java.util.List;
import java.util.UUID;

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

    @Delete
    public Completable deleteLocation(LocationModel location);

    @Query("DELETE FROM ManufacturerData WHERE manufacturerDeviceId=:id")
    public Completable deleteManufacturerDataForDeviceId(String id);

    @Delete
    public Completable deleteScanResult(ScanResultModel scanResult);

    @Query("SELECT * FROM BlacklistForTiles")
    public Single<List<BlacklistForTiles>> getBlacklistForTiles();

    @Query("DELETE FROM BlacklistForTiles")
    public Completable deleteAllTilesFromBlacklist();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertAllBlacklistForTiles(List<BlacklistForTiles> tiles);

    @Query("SELECT * FROM BlacklistForAirTagAndSmartTag WHERE id = 1")
    public Single<BlacklistForAirTagAndSmartTag> getBlacklistForAirTagsAndSmartTags();

    @Update
    public Completable updateBlacklistForAirTagAndSmartTag(BlacklistForAirTagAndSmartTag blacklist);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertBlacklistForAirTagAndSmartTag(BlacklistForAirTagAndSmartTag blacklist);
}
