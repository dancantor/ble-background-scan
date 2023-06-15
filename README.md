# ble-background-scan

Plugin to scan (even when app is in the background) for BLE devices

## Install

```bash
npm install ble-background-scan
npx cap sync
```

## API

<docgen-index>

* [`initiateBackgroundScan()`](#initiatebackgroundscan)
* [`initiateThreatDetection()`](#initiatethreatdetection)
* [`initiatePeriodicDataPurging()`](#initiateperiodicdatapurging)
* [`initialize()`](#initialize)
* [`setBlacklistForDevices(...)`](#setblacklistfordevices)
* [`getThreateningDevices()`](#getthreateningdevices)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initiateBackgroundScan()

```typescript
initiateBackgroundScan() => Promise<void>
```

--------------------


### initiateThreatDetection()

```typescript
initiateThreatDetection() => Promise<void>
```

--------------------


### initiatePeriodicDataPurging()

```typescript
initiatePeriodicDataPurging() => Promise<void>
```

--------------------


### initialize()

```typescript
initialize() => Promise<void>
```

--------------------


### setBlacklistForDevices(...)

```typescript
setBlacklistForDevices(blacklist: BlacklistForDevices) => Promise<void>
```

| Param           | Type                                                                |
| --------------- | ------------------------------------------------------------------- |
| **`blacklist`** | <code><a href="#blacklistfordevices">BlacklistForDevices</a></code> |

--------------------


### getThreateningDevices()

```typescript
getThreateningDevices() => Promise<PossibleThreateningTrackerList>
```

**Returns:** <code>Promise&lt;<a href="#possiblethreateningtrackerlist">PossibleThreateningTrackerList</a>&gt;</code>

--------------------


### Interfaces


#### BlacklistForDevices

| Prop                    | Type                |
| ----------------------- | ------------------- |
| **`tilesID`**           | <code>string</code> |
| **`airTagThreshold`**   | <code>number</code> |
| **`smartTagThreshold`** | <code>number</code> |


#### PossibleThreateningTrackerList

| Prop             | Type                                      |
| ---------------- | ----------------------------------------- |
| **`deviceList`** | <code>PossibleThreateningTracker[]</code> |


#### PossibleThreateningTracker

| Prop              | Type                            |
| ----------------- | ------------------------------- |
| **`deviceId`**    | <code>number</code>             |
| **`deviceModel`** | <code>string</code>             |
| **`locations`**   | <code>DetectedLocation[]</code> |


#### DetectedLocation

| Prop                   | Type                |
| ---------------------- | ------------------- |
| **`latitude`**         | <code>number</code> |
| **`longitude`**        | <code>number</code> |
| **`datetime`**         | <code>string</code> |
| **`locationDeviceId`** | <code>string</code> |

</docgen-api>
