package org.atlas.gateway.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.atlas.gateway.utils.OsUtils.OSType;

public class IpAddrUtils {

	private static final Logger logger = LogManager.getLogger(IpAddrUtils.class);
	private static final String ipCheckUrl = "http://checkip.amazonaws.com";
	
	private static final List<String> ignoreIps = Arrays.asList("127.0.0.1", "0.0.0.0", "0:0:0:0:0:0:0:1");
	
	/**
	 * Find the Public IP of the Gateway.
	 * @return	- String - Public IP of the Gateway.
	 * @throws ClientProtocolException 
	 * @throws UnknownHostException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getExternalIPAddress(){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(ipCheckUrl);
		httpGet.setConfig(RequestConfig.custom()
										.setConnectionRequestTimeout(10000)
										.setConnectTimeout(10000)
										.setSocketTimeout(10000)
										.build());
		CloseableHttpResponse ipResponse;
		try {
			ipResponse = httpclient.execute(httpGet);
			return EntityUtils.toString(ipResponse.getEntity(), "UTF-8").trim();
		} catch (IOException e) {
			logger.error("Unable to determine Gateway Public IP ",e);
		}
		return "";
	}
	
	public static HashMap<String, String> getLocalIpAddress(){
		OSType os = OsUtils.getOperatingSystemType();
		HashMap<String, String> ipAddresses = new HashMap<String, String>();
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)){
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					String temp[] = checkInterfaceAddress(os, inetAddress);
					if( (temp == null) || ( temp[0].equals("") || temp[1].equals("") ) ) continue;
					ipAddresses.put(temp[0], temp[1]);
				}
			}
			
		} catch (SocketException e) {
			logger.error("Unable to parse gateway local IP interfaces",e);
		}
		return ipAddresses;
	}
	
	public static ArrayList<String> getLocalIpAddressToArray(){
		OSType os = OsUtils.getOperatingSystemType();
		ArrayList<String> ipAddresses = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)){
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					String temp[] = checkInterfaceAddress(os, inetAddress);
					if( (temp == null) || ( temp[0].equals("") || temp[1].equals("") ) ) continue;
					ipAddresses.add(temp[1]);
				}
			}
			
		} catch (SocketException e) {
			logger.error("Unable to parse gateway local IP interfaces",e);
		}
		return ipAddresses;
	}
	
	public static String[] checkInterfaceAddress(OSType os, InetAddress inetAddress){
		if( ignoreIps.contains(inetAddress.getHostAddress()) ) return null;
		String ipInter = "";
		String ipAddr = "";
		if( inetAddress instanceof Inet4Address ){
			if( os == OSType.Windows ){
				ipAddr = inetAddress.getHostAddress();
			}else if( os == OSType.Linux ){
				ipAddr = inetAddress.getHostAddress();
				ipInter = ipAddr;
			}
		}else if( inetAddress instanceof Inet6Address ){
			if( os == OSType.Windows ){
				String temp[] = inetAddress.getHostAddress().split("%");
				ipAddr = temp[0];
				ipInter = temp[1];
			}else if( os == OSType.Linux ){
				//TODO on linux
			}
		}
		return new String[]{ipInter,ipAddr};
	}
	
}
