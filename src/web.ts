import { WebPlugin } from '@capacitor/core';

import type { BleBackgroundScanPlugin } from './definitions';
import type { BlacklistForDevices } from './models/blacklist-for-devices';
import type { PossibleThreateningTrackerList } from './models/possible-threatening-treacker';

export class BleBackgroundScanWeb extends WebPlugin implements BleBackgroundScanPlugin {
  initiatePeriodicDataPurging(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
  getThreateningDevices(): Promise<PossibleThreateningTrackerList> {
    throw this.unimplemented('Not implemented on web.');
  }
  setBlacklistForDevices(blacklist: BlacklistForDevices): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
  initiateThreatDetection(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
  initiateBackgroundScan(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  initialize(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
}
