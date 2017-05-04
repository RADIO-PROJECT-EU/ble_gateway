package org.atlas.wsn.ble.listener;

/**
 * BluetoothAdvertisementScanListener must be implemented by any class
 * wishing to receive BLE advertisement data
 *
 */
public interface BluetoothAdvertisementScanListener {

    /**
     * Fired when bluetooth advertisement data is received
     *
     * @param btAdData
     */
    public void onAdvertisementDataReceived(BluetoothAdvertisementData btAdData);
}
