package org.atlas.wsn.ble.impl;

import java.util.UUID;

import org.atlas.wsn.ble.BluetoothGattCharacteristic;

public class BluetoothGattCharacteristicImpl implements BluetoothGattCharacteristic {

    private final UUID m_uuid;
    private String m_handle;
    private int m_properties;
    private String m_valueHandle;

    public BluetoothGattCharacteristicImpl(String uuid, String handle, String properties, String valueHandle) {
        this.m_uuid = UUID.fromString(uuid);
        setHandle(handle);
        setProperties(Integer.parseInt(properties.substring(2, properties.length()), 16));
        setValueHandle(valueHandle);
    }

    // --------------------------------------------------------------------
    //
    // BluetoothGattCharacteristic API
    //
    // --------------------------------------------------------------------
    @Override
    public UUID getUuid() {
        return this.m_uuid;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {

    }

    @Override
    public int getPermissions() {
        return 0;
    }

    public void setHandle(String m_handle) {
        this.m_handle = m_handle;
    }

    public void setProperties(int m_properties) {
        this.m_properties = m_properties;
    }

    public void setValueHandle(String m_valueHandle) {
        this.m_valueHandle = m_valueHandle;
    }

    @Override
    public String getHandle() {
        return this.m_handle;
    }

    @Override
    public int getProperties() {
        return this.m_properties;
    }

    @Override
    public String getValueHandle() {
        return this.m_valueHandle;
    }

}
