package org.atlas.wsn.ble.util;

/**
 * For listening to btsnoop streams
 */
public interface BTSnoopListener {

    /**
     * Process a BTSnoop Record
     *
     * @param record
     */
    public void processBTSnoopRecord(byte[] record);

}
