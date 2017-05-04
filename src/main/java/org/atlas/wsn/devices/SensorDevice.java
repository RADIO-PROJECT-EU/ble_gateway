package org.atlas.wsn.devices;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.atlas.gateway.components.cloud.DataPublisher;
import org.atlas.gateway.connectors.SysLocalConnector;
import org.atlas.gateway.utils.DataUtils;
import org.atlas.gateway.utils.OsUtils;
import org.atlas.wsn.ble.BluetoothDevice;
import org.atlas.wsn.ble.BluetoothGatt;
import org.atlas.wsn.ble.BluetoothLeNotificationListener;
import org.atlas.wsn.ble.impl.BluetoothDeviceImpl;
import org.atlas.wsn.exceptions.WsnException;
import org.atlas.wsn.handlers.BleWatchDog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SensorDevice implements Runnable,BluetoothLeNotificationListener {

	private static final Logger logger = LoggerFactory.getLogger(SensorDevice.class); 
	
	private boolean isConnectable;
	private boolean connected = false;
	private BluetoothDevice device;
	private BluetoothGatt bleGatt;
	private ObjectMapper jsonMapper = new ObjectMapper();
	private int unityId=-1;
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public SensorDevice(String address, boolean connectable, int unityId){
		this.unityId = unityId;
		isConnectable = connectable;
		if( isConnectable  ){
			device = new BluetoothDeviceImpl(address, "");
			bleGatt = device.getBluetoothGatt();
			if( doConnect() ){
				connected = true;
				logger.info("Device("+address+") connected successfully!!!");
				bleGatt.setBluetoothLeNotificationListener(this);
				enableServices();
			}else{
				logger.error("Unable to connect to device : "+address);
			}
		}
	}

	private void enableServices() {
		if( !isConnectable ) return;
		
		if( connected ){
			
			File sensServicesExist = new File(OsUtils.getEnviromentalVariable("GATEWAY_HOME")+"/ti/"+device.getAdress().replaceAll(":", "")+".json");
			if( sensServicesExist.exists() ){
				try {
					SensorService[] services = this.jsonMapper.readValue(sensServicesExist, SensorService[].class);
					for( SensorService serv : services ){
						bleGatt.writeCharacteristicValue(serv.getHandle(), serv.getValue());
					}
				} catch (IOException e) {
					logger.error("Unable to parse device services: "+device.getAdress());
				}
			}
		}
	}

	@Override
	public void run() {
		scheduler.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {				
				logger.info("Checking connection with device...");
				try {
					if( !bleGatt.checkConnection() ){
						connected = false;
						connected = doConnect();
						enableServices();
					}
				} catch (WsnException e) {
					logger.error("Unable to check connection.",e);
					connected = false;
					bleGatt.disconnect();
					bleGatt = null;
					scheduler.shutdown();
				}
			}
		}, 10, 15, TimeUnit.SECONDS);
	}
	
	public boolean doConnect(){
		try {
			return bleGatt.connect();
		} catch (WsnException e) {
			logger.error("Unable to connect",e);
			return false;
		}
	}	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDataReceived(String handle, String value) {
		BleWatchDog.setLastMsgForDevive(device.getAdress(), System.currentTimeMillis());
		String datatype = DataUtils.getDatatype(handle);
		logger.info("Data from " + device.getAdress() + ", Datatype("+handle+" -> " + datatype + ")");
		if( datatype != null ){
			if(datatype.equals("movement")){
				SysLocalConnector.publish("wsn/data/"+datatype, value.getBytes(), 0);
			}else{
				double numericValue = DataUtils.transformDataToNumeric(datatype, value, "ti");
				ServiceData data = new ServiceData(device.getAdress(), datatype, numericValue);
				data.setPayload("data,"+this.unityId+","+datatype+","+numericValue);//TODO 1 should be the unityId of the sensor.
				DataPublisher.addData(data);				
			}
		}else{
			logger.warn("Unable to determine datatype of the given handle: " + handle);
		}
	}

	public boolean isConnectable() {
		return isConnectable;
	}

	public void setConnectable(boolean isConnectable) {
		this.isConnectable = isConnectable;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

}
