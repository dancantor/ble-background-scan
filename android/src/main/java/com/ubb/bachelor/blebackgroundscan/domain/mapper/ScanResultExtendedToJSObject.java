package com.ubb.bachelor.blebackgroundscan.domain.mapper;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.ubb.bachelor.blebackgroundscan.domain.model.LocationModel;
import com.ubb.bachelor.blebackgroundscan.domain.model.ScanResultExtended;

import java.util.List;

public class ScanResultExtendedToJSObject {
    public static JSObject mapLocationToJSObject(LocationModel location) {
        JSObject jsObject = new JSObject();
        jsObject.put("latitude", location.latitude);
        jsObject.put("longitude", location.longitude);
        jsObject.put("datetime", location.datetime.toString());
        jsObject.put("locationDeviceId", location.locationDeviceId);
        return jsObject;
    }

    public static JSArray mapLocationListToJSArray(List<LocationModel> locations) {
        JSArray jsArray = new JSArray();
        for(LocationModel location : locations) {
            jsArray.put(mapLocationToJSObject(location));
        }
        return jsArray;
    }

    public static JSObject mapPossibleThreateningDeviceToJSObject(ScanResultExtended scanResult) {
        JSObject jsObject = new JSObject();
        jsObject.put("deviceId", scanResult.scanResult.deviceId);
        jsObject.put("deviceModel", scanResult.scanResult.deviceModel);
        jsObject.put("locations", mapLocationListToJSArray(scanResult.location));
        return jsObject;
    }

    public static JSArray mapPossibleThreateningDeviceListToJSArray(List<ScanResultExtended> scanResults) {
        JSArray jsArray = new JSArray();
        for(ScanResultExtended scanResult : scanResults) {
            jsArray.put(mapPossibleThreateningDeviceToJSObject(scanResult));
        }
        return jsArray;
    }

    public static JSObject mapPossibleThreateningDeviceListToJSObject(List<ScanResultExtended> scanResults) {
        JSObject jsObject = new JSObject();
        jsObject.put("deviceList", mapPossibleThreateningDeviceListToJSArray(scanResults));
        return jsObject;
    }
}
