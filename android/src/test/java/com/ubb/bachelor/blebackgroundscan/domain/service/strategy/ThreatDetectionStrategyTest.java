package com.ubb.bachelor.blebackgroundscan.domain.service.strategy;

import static org.junit.Assert.assertEquals;

import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultModel;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreatDetectionStrategyTest {
    private ThreatDetectionStrategy threatDetectionStrategyConstant;
    private ThreatDetectionStrategy threatDetectionStrategyRotating;
    private List<ScanResultExtended> scanResultList;
    @Before
    public void initializeData() {
        this.threatDetectionStrategyConstant = new ConstantDeviceIdStrategy();
        threatDetectionStrategyRotating = new RotatingDeviceIdStrategy();
    }


    @Test
    public void testTilesNoDistanceNoTimeInterval() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0);
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.766643966937764;
        location2.longitude = 23.62662615551582;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 13, 20);
        List<LocationModel> locations = Arrays.asList(location1, location2);
        ScanResultExtended scanResult = new ScanResultExtended();
        scanResult.scanResult = new ScanResultModel();
        scanResult.scanResult.deviceModel = "Tile";
        scanResult.location = locations;
        List<ScanResultExtended> scanResults = List.of(scanResult);
        assertEquals(threatDetectionStrategyConstant.computeThreateningDevices(scanResults).size(), 0);
    }

    @Test
    public void testTilesRightDistanceNoTimeInterval() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0);
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.771320;
        location2.longitude = 23.582447;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 13, 20);
        List<LocationModel> locations = Arrays.asList(location1, location2);
        ScanResultExtended scanResult = new ScanResultExtended();
        scanResult.scanResult = new ScanResultModel();
        scanResult.scanResult.deviceModel = "Tile";
        scanResult.location = locations;
        List<ScanResultExtended> scanResults = List.of(scanResult);
        assertEquals(threatDetectionStrategyConstant.computeThreateningDevices(scanResults).size(), 0);
    }

    @Test
    public void testTilesNoDistanceRightTimeInterval() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0);
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.766643966937764;
        location2.longitude = 23.62662615551582;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 12, 59);
        List<LocationModel> locations = Arrays.asList(location1, location2);
        ScanResultExtended scanResult = new ScanResultExtended();
        scanResult.scanResult = new ScanResultModel();
        scanResult.scanResult.deviceModel = "Tile";
        scanResult.location = locations;
        List<ScanResultExtended> scanResults = List.of(scanResult);
        assertEquals(threatDetectionStrategyConstant.computeThreateningDevices(scanResults).size(), 0);
    }

    @Test
    public void testTilesRightDistanceRightTimeInterval() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0);
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.771320;
        location2.longitude = 23.582447;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 12, 59);
        List<LocationModel> locations = Arrays.asList(location1, location2);
        ScanResultExtended scanResult = new ScanResultExtended();
        scanResult.scanResult = new ScanResultModel();
        scanResult.scanResult.deviceModel = "Tile";
        scanResult.location = locations;
        List<ScanResultExtended> scanResults = List.of(scanResult);
        assertEquals(threatDetectionStrategyConstant.computeThreateningDevices(scanResults).size(), 1);
    }

    @Test
    public void testAirTagsNoDistanceNoTimeInterval() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0);
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.766643966937764;
        location2.longitude = 23.62662615551582;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 13, 15);
        List<LocationModel> locations1 = List.of(location1);
        List<LocationModel> locations2 = List.of(location2);
        ScanResultExtended scanResult1 = new ScanResultExtended();
        ScanResultExtended scanResult2 = new ScanResultExtended();
        scanResult1.scanResult = new ScanResultModel();
        scanResult2.scanResult = new ScanResultModel();
        scanResult1.scanResult.deviceModel = "AirTag";
        scanResult2.scanResult.deviceModel = "AirTag";
        scanResult1.location = locations1;
        scanResult2.location = locations2;
        List<ScanResultExtended> scanResults = Arrays.asList(scanResult1, scanResult2);
        assertEquals(threatDetectionStrategyRotating.computeThreateningDevices(scanResults).size(), 0);
    }

    @Test
    public void testAirTagsRightDistanceRightTimeInterval() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0);
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.771320;
        location2.longitude = 23.582447;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 12, 59);
        List<LocationModel> locations1 = List.of(location1);
        List<LocationModel> locations2 = List.of(location2);
        ScanResultExtended scanResult1 = new ScanResultExtended();
        ScanResultExtended scanResult2 = new ScanResultExtended();
        scanResult1.scanResult = new ScanResultModel();
        scanResult2.scanResult = new ScanResultModel();
        scanResult1.scanResult.deviceModel = "AirTag";
        scanResult2.scanResult.deviceModel = "AirTag";
        scanResult1.location = locations1;
        scanResult2.location = locations2;
        List<ScanResultExtended> scanResults = Arrays.asList(scanResult1, scanResult2);
        assertEquals(threatDetectionStrategyRotating.computeThreateningDevices(scanResults).size(), 2);
    }
}