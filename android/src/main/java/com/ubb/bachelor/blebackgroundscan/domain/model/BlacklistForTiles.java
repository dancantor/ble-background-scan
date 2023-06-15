package com.ubb.bachelor.blebackgroundscan.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "BlacklistForTiles")
public class BlacklistForTiles {
    @PrimaryKey
    @NonNull
    public String tileId;
}
