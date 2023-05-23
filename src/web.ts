import { WebPlugin } from '@capacitor/core';

import type { BleBackgroundScanPlugin } from './definitions';

export class BleBackgroundScanWeb extends WebPlugin implements BleBackgroundScanPlugin {
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
