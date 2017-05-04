package org.atlas.gateway.components.wsn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.atlas.gateway.components.cloud.CloudHandler;
import org.atlas.gateway.components.database.Database;
import org.atlas.gateway.components.database.models.Unity;
import org.atlas.gateway.components.mediator.Bridge;
import org.atlas.gateway.components.mediator.BridgeType;
import org.atlas.gateway.components.mediator.Subscription;
import org.atlas.gateway.components.mediator.processors.Processor;
import org.atlas.gateway.components.router.RouterImpl;
import org.atlas.gateway.components.wsn.messages.WSNMessage.Advertisment;
import org.atlas.gateway.components.wsn.messages.WSNMessage.Advertisment.WirelessTechnology;
import org.atlas.gateway.configurations.GatewayConfig;
import org.atlas.gateway.configurations.WSNConfig;
import org.atlas.gateway.connectors.AtlasConnection;
import org.atlas.gateway.connectors.AtlasConnectionFactory;
import org.atlas.gateway.connectors.SysLocalConnector;
import org.atlas.gateway.core.commands.Operation;
import org.atlas.gateway.core.exceptions.AtlasGwException;
import org.atlas.gateway.services.Router;
import org.atlas.gateway.utils.OsUtils;
import org.atlas.gateway.utils.OsUtils.OSType;
import org.atlas.wsn.ble.BluetoothAdapter;
import org.atlas.wsn.ble.impl.BluetoothServiceImpl;
import org.atlas.wsn.ble.listener.BluetoothAdvertisementData;
import org.atlas.wsn.ble.listener.BluetoothAdvertisementScanListener;
import org.atlas.wsn.devices.SensorDevice;
import org.atlas.wsn.handlers.BleWatchDog;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.ByteString;

@Component
public class WSNHandler implements BluetoothAdvertisementScanListener {

	private static final Logger logger = LoggerFactory.getLogger(WSNHandler.class);
	private BluetoothAdapter bleAdapter;
	private ExecutorService connectedDevicesExecutor;
	private long lastAdvertismentReceived = 0;//In milliseconds
	private long threshold = 20000;//In milliseconds
	
	private HashMap<String, SensorDevice> bleDevices;
	
	private String[] knownDevices = {"B0:B4:48:C9:26:01","B0:B4:48:ED:86:00","B0:B4:48:C9:52:03","B0:B4:48:ED:99:01","24:71:89:07:92:07"};
	
	@Autowired
	private Database database;

	
	@PostConstruct
	public void bootUpWSNHandler(){
		this.bleDevices = new HashMap<String, SensorDevice>();
		BluetoothServiceImpl bleService = new BluetoothServiceImpl();
		bleAdapter = bleService.getBluetoothAdapter();
		if( bleAdapter != null ){
			logger.info("Local bluetooth device address: "+bleAdapter.getAddress());
			if( !bleAdapter.isEnabled() ){
				logger.info("BLE Adapter is not enabled, going to enable it...");
				bleAdapter.enable();
				logger.info("BLE Adapter enabled succefully...");
			}
			logger.info("Starting advertisements scanning...");
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			this.connectedDevicesExecutor = Executors.newFixedThreadPool(7);//Because of maxixum connected devices.
			scheduler.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					logger.info("Checking Advertismemtns Handler status....");
					enableAdvertismentsHandler();
					checkDevicesConnection();
				}
			}, 10, 15, TimeUnit.SECONDS);
         }else{
        	 logger.warn("Unable to find local BLE Adapter");
         } 
	}	
	
	private void checkDevicesConnection() {
		Iterator it = this.bleDevices.entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        SensorDevice device = (SensorDevice) pair.getValue();
			if( device.isConnectable() ){
				long lstMsg = BleWatchDog.getLastMsgFromDevice(device.getDevice().getAdress());
				if( lstMsg > 30000 ) it.remove();
			}
	    }
	}
	
	private void enableAdvertismentsHandler(){
		if( this.bleAdapter.isScanning() ){
			//Reason unknown scanning is stopped.
			if( (Calendar.getInstance().getTimeInMillis() - this.lastAdvertismentReceived) > this.threshold ){
				logger.info("Not received advertisments for "+this.threshold+" milliseconds, re-start, Last packet: " + new Date(this.lastAdvertismentReceived));
				this.bleAdapter.killLeScan();
				this.bleAdapter.startAdvertisementScan("000D", this);
			}
		}else{
			logger.info("Not scanning for advertisment, enable them.");
			this.bleAdapter.startAdvertisementScan("000D", this);
		}
	}

	@Override
	public void onAdvertisementDataReceived(BluetoothAdvertisementData btAdData) {
		String deviceAddress = btAdData.getReportRecords().get(0).getAddress();
		this.lastAdvertismentReceived = Calendar.getInstance().getTimeInMillis();
		logger.info("Message received from device address: " + deviceAddress);
		
		switch( btAdData.getReportRecords().get(0).getEventType() ){
			case 0:
				logger.info("Packet Type: ADV_IND, Submitting request for connecting with the device.");
				if( ArrayUtils.contains(knownDevices, deviceAddress) && this.bleDevices.get(deviceAddress) == null ){
					SensorDevice device = new SensorDevice(deviceAddress,true, getUnityMap(deviceAddress));
					this.bleDevices.put(deviceAddress, device);
					this.connectedDevicesExecutor.execute(device);
				}
				break;
			
			case 1:
				logger.info("Packet Type: ADV_DIRECT_IND, Ignoring");
				break;
			
			case 2:
				logger.info("Packet Type: ADV_SCAN_IND, Ignoring...");
				break;
				
			case 3:
				logger.info("Packet Type: ADV_NONCONN_IND, Forwarding messages to Topic...");
				Advertisment advertisment = Advertisment
						.newBuilder()
						.setAddress(deviceAddress)
						.setTechnology(WirelessTechnology.BLE)
						.setData(ByteString.copyFrom(btAdData.getRawData()))
						.build();
				SysLocalConnector.publish("wsn/ble/devices/advertisments", advertisment.toByteArray(),1);
				break;
				
			case 4:
				logger.info("Packet Type: SCAN_RSP, Ignoring...");
				break;
				
			default:
				logger.warn("Unimplemented...");
		
		}
		
	}
	
	@PreDestroy
	public void shutDownHandler(){
		//TODO
	}
	
	private int getUnityMap(String address) {
		ResultSetHandler<Unity> h = new BeanHandler<Unity>(Unity.class);
		try {
			Unity model = database.getSQLRunner().query("SELECT * FROM unity WHERE address=?", h, address);
			if( model == null ) return -1;
				return model.getSensorId();
			} catch (SQLException e) {
				logger.error("Unable to retrieve device...",e);
			}
		return -1;
	}
	
}
