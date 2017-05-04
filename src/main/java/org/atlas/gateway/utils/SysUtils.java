package org.atlas.gateway.utils;

public class SysUtils {
	
	/**
	 * Parse a MAC address of type 00:00:00:00:00:00 and return 00000000000
	 * @return	
	 */
	public static String stringifyMacAddress(String mac) {
	    return mac.replaceAll(":", "");
	}

}
