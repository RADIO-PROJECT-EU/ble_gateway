package org.atlas.wsn.ble.impl;

import java.util.List;
import java.util.UUID;

import org.atlas.wsn.ble.BluetoothGattCharacteristic;
import org.atlas.wsn.ble.BluetoothGattService;

public class BluetoothGattServiceImpl implements BluetoothGattService {

    private final UUID m_uuid;
    private final String m_startHandle;
    private final String m_endHandle;

    public BluetoothGattServiceImpl(String uuid, String startHandle, String endHandle) {
        this.m_uuid = UUID.fromString(uuid);
        this.m_startHandle = startHandle;
        this.m_endHandle = endHandle;
    }

    // --------------------------------------------------------------------
    //
    // BluetoothGattService API
    //
    // --------------------------------------------------------------------

    @Override
    public BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        return null;
    }

    @Override
    public List<BluetoothGattCharacteristic> getCharacterisitcs() {
        return null;
    }

    @Override
    public UUID getUuid() {
        return this.m_uuid;
    }

    @Override
    public String getStartHandle() {
        return this.m_startHandle;
    }

    @Override
    public String getEndHandle() {
        return this.m_endHandle;
    }
}
