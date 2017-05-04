package org.atlas.wsn.ble.impl;

import java.util.Date;

import org.atlas.wsn.ble.BluetoothConnector;
import org.atlas.wsn.ble.BluetoothDevice;
import org.atlas.wsn.ble.BluetoothGatt;
import org.atlas.wsn.ble.impl.BluetoothGattImpl;

public class BluetoothDeviceImpl implements BluetoothDevice {

    public static final int DEVICE_TYPE_DUAL = 0x003;
    public static final int DEVICE_TYPE_LE = 0x002;
    public static final int DEVICE_TYPE_UNKNOWN = 0x000;

    private final String m_name;
    private final String m_address;
    private final Date dateScanned;

    public BluetoothDeviceImpl(String address, String name) {
        this.m_address = address;
        this.m_name = name;
        this.dateScanned = new Date();
    }

    // --------------------------------------------------------------------
    //
    // BluetoothDevice API
    //
    // --------------------------------------------------------------------
    @Override
    public String getName() {
        return this.m_name;
    }

    @Override
    public String getAdress() {
        return this.m_address;
    }

    @Override
    public int getType() {
        return DEVICE_TYPE_UNKNOWN;
    }

    @Override
    public BluetoothConnector getBluetoothConnector() {
    	return null;
    }

    @Override
    public BluetoothGatt getBluetoothGatt() {
        return new BluetoothGattImpl(this.m_address);
    }

	@Override
	public Date getDateScanned() {
		return this.dateScanned;
	}

}
