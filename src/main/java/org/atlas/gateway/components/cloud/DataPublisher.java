package org.atlas.gateway.components.cloud;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.atlas.wsn.devices.ServiceData;

public class DataPublisher {

	private static LinkedBlockingQueue<ServiceData> cloudData = new LinkedBlockingQueue<ServiceData>();
	
	public static void addData(ServiceData data){
		cloudData.add(data);
	}
	
	public static ArrayList<ServiceData> getData(){
		ArrayList<ServiceData> list = new ArrayList<ServiceData>();
		cloudData.drainTo(list);
		return list;
	}
}
