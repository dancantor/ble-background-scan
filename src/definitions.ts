import type { BlacklistForDevices } from './models/blacklist-for-devices';
import type { PossibleThreateningTrackerList } from './models/possible-threatening-treacker';

export type CallbackID = string;

export interface BleBackgroundScanPlugin {
  initiateBackgroundScan(): Promise<void>;
  initiateThreatDetection(): Promise<void>;
  initiatePeriodicDataPurging(): Promise<void>;
  initialize(): Promise<void>;
  setBlacklistForDevices(blacklist: BlacklistForDevices): Promise<void>;
  getThreateningDevices(): Promise<PossibleThreateningTrackerList>;
}

export type BleDeviceCallback = (value: number | null, err?: any) => void;
