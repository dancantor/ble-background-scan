package com.ubb.bachelor.blebackgroundscan.domain.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "BlacklistForAirTagAndSmartTag")
public class BlacklistForAirTagAndSmartTag {
    @PrimaryKey
    @NonNull
    public int id;
    @ColumnInfo(name = "ignoredAirTags")
    public int ignoredAirTags;

    @ColumnInfo(name = "ignoredSmartTags")
    public int ignoredSmartTags;
}
