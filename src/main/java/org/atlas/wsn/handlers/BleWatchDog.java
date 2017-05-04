package org.atlas.wsn.handlers;

import java.util.Date;
import java.util.HashMap;

public class BleWatchDog {
	
	private static HashMap<String, Long> lastMsg = new HashMap<String, Long>();
	
	public static void setLastMsgForDevive(String address, long timestamp){
		lastMsg.put(address, timestamp);
	}

	/**
	 * Return the last message in milliseconds now - lastMsg
	 * @param address
	 * @return
	 */
	public static long getLastMsgFromDevice(String address){
		long now = System.currentTimeMillis();
		long lastPing = lastMsg.get(address);
		return now-lastPing;
	}
	
}
