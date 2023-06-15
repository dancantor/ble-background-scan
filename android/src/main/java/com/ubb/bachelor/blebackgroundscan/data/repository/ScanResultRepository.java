package com.ubb.bachelor.blebackgroundscan.data.repository;

import android.content.Context;

import androidx.room.Room;

import com.ubb.bachelor.blebackgroundscan.data.data_source.ScanRecordDao;
import com.ubb.bachelor.blebackgroundscan.data.data_source.ScanRecordDatabase;
import com.ubb.bachelor.blebackgroundscan.domain.exception.ScanRecordRepositoryNotInitialized;
import com.ubb.bachelor.blebackgroundscan.domain.model.BlacklistForAirTagAndSmartTag;
import com.ubb.bachelor.blebackgroundscan.domain.model.BlacklistForTiles;
import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ManufacturerData;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Single;

public class ScanResultRepository {
    private static ScanResultRepository repository;
    private ScanRecordDao dao;


    private ScanResultRepository(Context context) {
        dao = Room.databaseBuilder(
                context,
                ScanRecordDatabase.class,
                "bluetooth-le"
        ).build().scanRecordDao();
    }


     public static ScanResultRepository getInstance(Context context) {
        if (ScanResultRepository.repository == null) {
            ScanResultRepository.repository = new ScanResultRepository(context);
        }
        return ScanResultRepository.repository;
     }

    public static ScanResultRepository getInstanceIfAvailable() throws ScanRecordRepositoryNotInitialized {
        if (ScanResultRepository.repository == null) {
            throw new ScanRecordRepositoryNotInitialized("Repository not initialized");
        }
        return ScanResultRepository.repository;
    }

    public Single<List<ScanResultExtended>> getScannedDevicesByModel(String deviceModel) {
        return dao.getScanResultsByModel(deviceModel);
    }

    public Completable insertScanResult(ScanResultExtended scanResult) {
        List<Completable> completableList = new ArrayList<Completable>();
        completableList.add(dao.insertScanResult((scanResult.scanResult)));
        for(ManufacturerData data : scanResult.manufacturerData) {
            completableList.add(dao.insertManufacturerData(data));
        }
        for(LocationModel location : scanResult.location) {
            completableList.add(dao.insertLocation(location));
        }
        return Completable.merge(completableList);
    }

    public Single<List<ScanResultExtended>> getScanResults() {
        return dao.getScanResults();
    }

    public Single<Boolean> isScanResultInTileBlacklist(ScanResultExtended scanResult) {
        return dao.getBlacklistForTiles()
                .map(blacklistForTiles ->
                        blacklistForTiles
                                .stream()
                                .anyMatch(ignoredDevice ->
                                        ignoredDevice.tileId.equals(scanResult.scanResult.deviceId)
                                )
                );
    }

    public Completable deleteAllTilesFromBlacklist() {
        return dao.deleteAllTilesFromBlacklist();
    }

    public Completable bulkInsertBlacklistForTiles(List<BlacklistForTiles> blacklistForTiles) {
        return dao.insertAllBlacklistForTiles(blacklistForTiles);
    }

    public Single<BlacklistForAirTagAndSmartTag> getBlacklistForAirTagAndSmartTag() {
        return dao.getBlacklistForAirTagsAndSmartTags();
    }

    public Completable updateBlacklistForAirTagAndSmartTag(BlacklistForAirTagAndSmartTag blacklist) {
        return dao.updateBlacklistForAirTagAndSmartTag(blacklist);
    }

    public Completable insertBlacklistForAirTagAndSmartTag(BlacklistForAirTagAndSmartTag blacklist) {
        return dao.insertBlacklistForAirTagAndSmartTag(blacklist);
    }

    public Completable deleteLocation(LocationModel location) {
        return dao.deleteLocation(location);
    }

    public Completable deleteManufacturerDataByDeviceId(String deviceId) {
        return dao.deleteManufacturerDataForDeviceId(deviceId);
    }

    public Completable deleteScanResult(ScanResultModel scanResult) {
        return dao.deleteScanResult(scanResult);
    }

}

