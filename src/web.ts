import { WebPlugin } from '@capacitor/core';

import type { BleBackgroundScanPlugin } from './definitions';

export class BleBackgroundScanWeb extends WebPlugin implements BleBackgroundScanPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
