export type CallbackID = string;

export interface BleBackgroundScanPlugin {
  initiateBackgroundScan(): Promise<void>;
  initiateThreatDetection(): Promise<void>;
  initialize(): Promise<void>
}

export type BleDeviceCallback = (value: number | null, err?: any) => void;
