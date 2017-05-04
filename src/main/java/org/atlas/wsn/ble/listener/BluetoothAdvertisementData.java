package org.atlas.wsn.ble.listener;

import java.util.ArrayList;
import java.util.List;

public class BluetoothAdvertisementData {

    private byte[] rawData;
    private byte packetType;
    private byte eventType;
    private int parameterLength;
    private byte subEventCode;
    private int numberOfReports;

    private List<AdvertisingReportRecord> reportRecords;

    public byte[] getRawData() {
        return this.rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = new byte[rawData.length];
        System.arraycopy(rawData, 0, this.rawData, 0, rawData.length);
    }

    public byte getPacketType() {
        return this.packetType;
    }

    public void setPacketType(byte packetType) {
        this.packetType = packetType;
    }

    public byte getEventType() {
        return this.eventType;
    }

    public void setEventType(byte eventType) {
        this.eventType = eventType;
    }

    public int getParameterLength() {
        return this.parameterLength;
    }

    public void setParameterLength(int parameterLength) {
        this.parameterLength = parameterLength;
    }

    public byte getSubEventCode() {
        return this.subEventCode;
    }

    public void setSubEventCode(byte subEventCode) {
        this.subEventCode = subEventCode;
    }

    public int getNumberOfReports() {
        return this.numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }

    public List<AdvertisingReportRecord> getReportRecords() {
        return this.reportRecords;
    }

    public void addReportRecord(AdvertisingReportRecord advertisingReportRecord) {
        if (this.reportRecords == null) {
            this.reportRecords = new ArrayList<AdvertisingReportRecord>();
        }
        this.reportRecords.add(advertisingReportRecord);
    }
}
