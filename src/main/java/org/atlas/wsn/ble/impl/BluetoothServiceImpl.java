package org.atlas.wsn.ble.impl;

import org.atlas.wsn.exceptions.WsnException;
import org.atlas.wsn.ble.BluetoothAdapter;
import org.atlas.wsn.ble.BluetoothBeaconCommandListener;
import org.atlas.wsn.ble.BluetoothService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothServiceImpl implements BluetoothService {

    private static final Logger s_logger = LoggerFactory.getLogger(BluetoothServiceImpl.class);

    // --------------------------------------------------------------------
    //
    // Service APIs
    //
    // -------------------------------------------------------------------
    @Override
    public BluetoothAdapter getBluetoothAdapter() {
        return getBluetoothAdapter("hci0");
    }

    @Override
    public BluetoothAdapter getBluetoothAdapter(String name) {
        try {
            return new BluetoothAdapterImpl(name);
        } catch (WsnException e) {
            s_logger.error("Could not get bluetooth adapter", e);
            return null;
        }
    }

    @Override
    public BluetoothAdapter getBluetoothAdapter(String name, BluetoothBeaconCommandListener bbcl) {
        try {
            BluetoothAdapterImpl bbs = new BluetoothAdapterImpl(name, bbcl);
            return bbs;
        } catch (WsnException e) {
            s_logger.error("Could not get bluetooth beacon service", e);
            return null;
        }
    }

}
