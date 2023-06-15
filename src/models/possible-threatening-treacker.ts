export interface PossibleThreateningTrackerList {
    deviceList: PossibleThreateningTracker[]
}

export interface PossibleThreateningTracker {
    deviceId: number;
    deviceModel: string
    locations: DetectedLocation[]
}

export interface DetectedLocation {
    latitude: number;
    longitude: number;
    datetime: string;
    locationDeviceId: string
}