package com.ubb.bachelor.blebackgroundscan.domain.service.strategy;

import android.annotation.SuppressLint;

import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ThreatDetectionStrategy {
    public abstract List<ScanResultExtended> computeThreateningDevices(List<ScanResultExtended> allScanResults);

    protected double getMaximumTimeRange(List<ScanResultExtended> allScanResults) {
        long maxHoursInterval = 0;
        for(ScanResultExtended scanResultExtended : allScanResults) {
            for (int i = 0; i < scanResultExtended.location.size(); ++i) {
                for (int j = i + 1; j < scanResultExtended.location.size(); ++j) {
                    LocationModel location1 = scanResultExtended.location.get(i);
                    LocationModel location2 = scanResultExtended.location.get(j);
                    if (computeHourIntervalBetweenTwoDates(location1.datetime, location2.datetime) >= maxHoursInterval) {
                        maxHoursInterval = computeHourIntervalBetweenTwoDates(location1.datetime, location2.datetime);
                    }
                }
            }
        }
        return maxHoursInterval;
    }

    protected double getMaximumDistance(List<ScanResultExtended> allScanResults) {
        double maxDistance = 0;
        for(ScanResultExtended scanResultExtended : allScanResults) {
            for (int i = 0; i < scanResultExtended.location.size(); ++i) {
                for (int j = i + 1; j < scanResultExtended.location.size(); ++j) {
                    LocationModel location1 = scanResultExtended.location.get(i);
                    LocationModel location2 = scanResultExtended.location.get(j);
                    if (computeDistanceBetweenTwoBeacons(location1, location2) >= maxDistance) {
                        maxDistance = computeDistanceBetweenTwoBeacons(location1, location2);
                    }
                }
            }
        }
        return maxDistance;
    }

    private long computeHourIntervalBetweenTwoDates(LocalDateTime date1, LocalDateTime date2) {
        return Math.abs(ChronoUnit.HOURS.between(date1, date2));
    }

    private double computeDistanceBetweenTwoBeacons(LocationModel location1, LocationModel location2) {
        double lon1 = Math.toRadians(location1.longitude);
        double lon2 = Math.toRadians(location2.longitude);
        double lat1 = Math.toRadians(location1.latitude);
        double lat2 = Math.toRadians(location2.latitude);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

}
