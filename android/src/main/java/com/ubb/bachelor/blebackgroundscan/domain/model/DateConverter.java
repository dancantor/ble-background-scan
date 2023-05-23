package com.ubb.bachelor.blebackgroundscan.domain.model;

import android.annotation.SuppressLint;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateConverter {
    static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @TypeConverter
    public static LocalDateTime toDate(String value) {
        if (value != null) {
            return LocalDateTime.parse(value, df);
        }
        return null;
    }

    @TypeConverter
    public static String fromDate(LocalDateTime value) {
        if (value != null) {
            return value.format(df);
        } else {
            return null;
        }
    }
}
