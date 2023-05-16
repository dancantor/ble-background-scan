export interface BleBackgroundScanPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
