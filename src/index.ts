import { registerPlugin } from '@capacitor/core';

import type { BleBackgroundScanPlugin } from './definitions';

const BleBackgroundScan = registerPlugin<BleBackgroundScanPlugin>('BleBackgroundScan', {
  web: () => import('./web').then(m => new m.BleBackgroundScanWeb()),
});

export * from './definitions';
export { BleBackgroundScan };
