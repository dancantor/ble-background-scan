package com.ubb.bachelor.blebackgroundscan.data.data_source;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ubb.bachelor.blebackgroundscan.domain.model.DateConverter;
import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ManufacturerData;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultModel;

import java.util.Arrays;

@Database(
        entities = {ScanResultModel.class, ManufacturerData.class, LocationModel.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class ScanRecordDatabase extends RoomDatabase {
    public abstract ScanRecordDao scanRecordDao();
}
