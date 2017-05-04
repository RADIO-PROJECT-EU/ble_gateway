package org.atlas.gateway.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.atlas.gateway.supervisor.MemoryType;
import org.atlas.gateway.supervisor.SysCpus;
import org.atlas.gateway.supervisor.SysGeneral;
import org.atlas.gateway.supervisor.SysMemory;
import org.atlas.gateway.supervisor.SysTasks;
import org.atlas.gateway.supervisor.TopInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsUtils {

	private static final Logger logger = LoggerFactory.getLogger(OsUtils.class);
	
	/**
	 * Operating Systems types
	 */
	public enum OSType {
		Windows, MacOS, Linux, Other
	};
	private static OSType detectedOS = null;
	private static String TOPCOMMAND = "top -n 1 -d 0 -b";
	private static String FREE_MEM_IDENTIFIER = "mem:";
	private static String FREE_SWAP_IDENTIFIER = "swap:";
	private static final String GATEWAY_HOME = "GATEWAY_HOME";
	
	/**
	 * Return the operating system that applications is running
	 * @return
	 */
	public static OSType getOperatingSystemType() {
	    if (detectedOS == null) {
	    	String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
	    	if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
	    		detectedOS = OSType.MacOS;
	    	} else if (OS.indexOf("win") >= 0) {
	    		detectedOS = OSType.Windows;
	    	} else if (OS.indexOf("nux") >= 0) {
	    		detectedOS = OSType.Linux;
	    	} else {
	    		detectedOS = OSType.Other;
	    	}
	    }
	    return detectedOS;
	}
	
	/**
	 * Convert byte to megabytes
	 * @param bytes
	 * @return
	 */
	public static String convertBytesToReadableFormat(long bytes, boolean si){
		int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	/**
	 * Get Operating system total memory
	 */
	public static ArrayList<SysMemory> getOperatingSystemMemory(){
		ArrayList<SysMemory> availableMemories = new ArrayList<>();
		Process p = null; 
		try {
			p = Runtime.getRuntime().exec("/usr/bin/free");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null) {
				if( line.toLowerCase().startsWith(FREE_MEM_IDENTIFIER) ){
					availableMemories.add(new SysMemory(line, MemoryType.RAM));
				}else if( line.toLowerCase().startsWith(FREE_SWAP_IDENTIFIER) ){
					availableMemories.add(new SysMemory(line, MemoryType.SWAP));
				}
			}
		} catch (IOException e) {
			logger.error("Unable to parse OS Memory info",e);
		}
		return availableMemories;
	}
	
	/**
	 * Linux top command parser
	 */
	public static List<TopInfo> getOsInfo(){
		ArrayList<TopInfo> information = new ArrayList<>();
		String stopSignal = "PID";
		boolean shouldRead = true;
		Process p = null; 
		try {
			p = Runtime.getRuntime().exec(TOPCOMMAND);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null) {
				if( line.contains(stopSignal) ) shouldRead = false;
				if(shouldRead){
					if( line.toLowerCase().startsWith("top") ){
						information.add(new SysGeneral(line));
					}else if( line.toLowerCase().startsWith("tasks:") ){
						information.add(new SysTasks(line));
					}else if( line.toLowerCase().startsWith("%cpu(s):") ){
						information.add(new SysCpus(line));
					}
				}
			}
		} catch (IOException e) {
			logger.error("Unable to parse OS Overall info",e);
		}
		return information;
	}
	
	public static String getEnviromentalVariable(String variable){
		logger.info("Checking for Environmental variable: " + variable);
		Map<String, String> env = System.getenv();
		String envVariable = env.get(variable);
		if( envVariable == null ){
			logger.warn("Environmental variable not found...");
		}
		if( variable.equals(GATEWAY_HOME) && envVariable == null  ){
			logger.warn("Environmental variable ("+variable+") not found...");
			return System.getProperty("user.dir");
		}
		
		logger.info("Environmental Variable ( "+variable+" ) found with value : " + envVariable);
        
		return envVariable;
	}
	
}
