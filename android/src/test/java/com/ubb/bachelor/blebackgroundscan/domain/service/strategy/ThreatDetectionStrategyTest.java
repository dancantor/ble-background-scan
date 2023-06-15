package com.ubb.bachelor.blebackgroundscan.domain.service.strategy;

import static org.junit.Assert.assertEquals;

import android.util.Log;

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
    private RotatingDeviceIdStrategy threatDetectionStrategyRotating;
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
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 20);
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
    public void testAirTagsRightDistanceRightTimeInterval() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 0);
        location1.locationDeviceId = "a";
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.771320;
        location2.longitude = 23.582447;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 1);
        location2.locationDeviceId = "b";
        LocationModel location3 = new LocationModel();
        location3.latitude = 46.771320;
        location3.longitude = 23.582447;
        location3.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 2);
        location3.locationDeviceId = "a";
        LocationModel location4 = new LocationModel();
        location4.latitude = 46.771320;
        location4.longitude = 23.582447;
        location4.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 15, 30);
        location4.locationDeviceId = "c";
        LocationModel location5 = new LocationModel();
        location5.latitude = 46.771320;
        location5.longitude = 23.582447;
        location5.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 40, 30);
        location5.locationDeviceId = "d";
        LocationModel location6 = new LocationModel();
        location6.latitude = 46.78038590997958;
        location6.longitude = 23.63439803662073;
        location6.datetime = LocalDateTime.of(2023, Month.MAY, 23, 17, 46, 30);
        location6.locationDeviceId = "b";
        List<LocationModel> locations1 = Arrays.asList(location1, location2, location3, location4, location5, location6);
        ScanResultExtended scanResult1 = new ScanResultExtended();
        scanResult1.scanResult = new ScanResultModel();
        scanResult1.scanResult.deviceModel = "AirTag";
        scanResult1.location = locations1;
        ScanResultExtended scanResult2 = new ScanResultExtended();
        scanResult2.scanResult = new ScanResultModel();
        scanResult2.scanResult.deviceModel = "AirTag";
        scanResult2.location = locations1;
        ScanResultExtended scanResult3 = new ScanResultExtended();
        scanResult3.scanResult = new ScanResultModel();
        scanResult3.scanResult.deviceModel = "AirTag";
        scanResult3.location = locations1;
        List<ScanResultExtended> scanResults = Arrays.asList(scanResult1, scanResult2);
        assertEquals(threatDetectionStrategyRotating.computeThreateningDevices(scanResults).size(), 1);
    }

    @Test
    public void testRotatingThreatDetectionFiltering() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 0);
        location1.locationDeviceId = "a";
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.771320;
        location2.longitude = 23.582447;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 1);
        location2.locationDeviceId = "b";
        LocationModel location3 = new LocationModel();
        location3.latitude = 46.771320;
        location3.longitude = 23.582447;
        location3.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 2);
        location3.locationDeviceId = "a";
        List<LocationModel> locations1 = Arrays.asList(location1, location2, location3);
        ScanResultExtended scanResult1 = new ScanResultExtended();
        scanResult1.scanResult = new ScanResultModel();
        scanResult1.scanResult.deviceModel = "AirTag";
        scanResult1.location = locations1;
        ScanResultExtended scanResult2 = new ScanResultExtended();
        scanResult2.scanResult = new ScanResultModel();
        scanResult2.scanResult.deviceModel = "AirTag";
        scanResult2.location = locations1;
        ScanResultExtended scanResult3 = new ScanResultExtended();
        scanResult3.scanResult = new ScanResultModel();
        scanResult3.scanResult.deviceModel = "AirTag";
        scanResult3.location = locations1;
        List<ScanResultExtended> scanResults = Arrays.asList(scanResult1, scanResult2);
        threatDetectionStrategyRotating.computeThreateningDevices(scanResults);
    }

    @Test
    public void testRotatingThreatDetectionGrouping() {
        LocationModel location1 = new LocationModel();
        location1.latitude = 46.766154879158385;
        location1.longitude = 23.629581474305557;
        location1.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 0);
        location1.locationDeviceId = "a";
        LocationModel location2 = new LocationModel();
        location2.latitude = 46.771320;
        location2.longitude = 23.582447;
        location2.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 1);
        location2.locationDeviceId = "b";
        LocationModel location3 = new LocationModel();
        location3.latitude = 46.771320;
        location3.longitude = 23.582447;
        location3.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 0, 2);
        location3.locationDeviceId = "a";
        LocationModel location4 = new LocationModel();
        location4.latitude = 46.771320;
        location4.longitude = 23.582447;
        location4.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 15, 30);
        location4.locationDeviceId = "c";
        LocationModel location5 = new LocationModel();
        location5.latitude = 46.771320;
        location5.longitude = 23.582447;
        location5.datetime = LocalDateTime.of(2023, Month.MAY, 23, 15, 40, 30);
        location5.locationDeviceId = "d";
        LocationModel location6 = new LocationModel();
        location6.latitude = 46.78038590997958;
        location6.longitude = 23.63439803662073;
        location6.datetime = LocalDateTime.of(2023, Month.MAY, 23, 17, 40, 30);
        location6.locationDeviceId = "b";
        List<LocationModel> locations1 = Arrays.asList(location1, location2, location3, location4, location5, location6);
        ScanResultExtended scanResult1 = new ScanResultExtended();
        scanResult1.scanResult = new ScanResultModel();
        scanResult1.scanResult.deviceModel = "AirTag";
        scanResult1.location = locations1;
        ScanResultExtended scanResult2 = new ScanResultExtended();
        scanResult2.scanResult = new ScanResultModel();
        scanResult2.scanResult.deviceModel = "AirTag";
        scanResult2.location = locations1;
        ScanResultExtended scanResult3 = new ScanResultExtended();
        scanResult3.scanResult = new ScanResultModel();
        scanResult3.scanResult.deviceModel = "AirTag";
        scanResult3.location = locations1;
        List<ScanResultExtended> scanResults = Arrays.asList(scanResult1, scanResult2);
        threatDetectionStrategyRotating.computeThreateningDevices(scanResults);
    }
}