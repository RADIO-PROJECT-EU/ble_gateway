package org.atlas.gateway.core.commands;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.atlas.gateway.components.database.Database;
import org.atlas.gateway.components.database.models.Application;
import org.atlas.gateway.components.database.models.DeviceDbModel;
import org.atlas.gateway.components.mediator.processors.Processor;
import org.atlas.gateway.components.watchers.WatchDog;
import org.atlas.gateway.components.wsn.WSNHandler;
import org.atlas.gateway.configurations.ApplicationsConfig;
import org.atlas.gateway.core.commands.GwCommand;
import org.atlas.gateway.services.CloudPublisher;
import org.atlas.gateway.services.MonitoringService;
import org.atlas.gateway.supervisor.Monitoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CommandProcessor implements Processor{

	private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	private final String SOURCE_IDENTIFIER = "commands_response";
	
	@Autowired
	private MonitoringService monitoringService;
	
	@Autowired
	private CloudPublisher cloudPublisher;
	
	@Autowired
	private WSNHandler wsnHandler;
	
	@Autowired
	private WatchDog watchDog;
	
	@Autowired
	private ApplicationsConfig appConfig;
	
	@Autowired
	private Database database;
	
	@PostConstruct
	public void initialize(){
		this.jsonMapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	@Override
	public void processMessage(byte[] message) {		
		try {
			GwCommand command = this.jsonMapper.readValue(message, GwCommand.class);
			if( command == null || 
					command.getUuid() == null || 
					command.getType() == null
			){
				logger.warn("Invalid command message received");
				return;
			}
			CommandResponse response = new CommandResponse();
			response.setUuid(command.getUuid());
			response.setType(command.getType());
			response.setOperation(command.getOperation());			
			switch( command.getType() ){
			
				case WIRELESS_MODE:
					if( command.getOperation() != null ){
						try {
							this.database.getSQLRunner().update( "UPDATE gwinfo SET configValue=?, updatedAt=datetime() WHERE configId=?", command.getOperation().toString() ,"ble_mode");
							response.addResult(new CommandResult(ResultStatus.EXECUTED, "Connection submitted for switching.."));
						} catch (SQLException e) {
							logger.error("Unable to update wireless connection mode...",e);
							response.addResult(new CommandResult(ResultStatus.ERROR, "Unable to update wireless connection mode.."));
						}
					}else{
						logger.error("Invalid command message received");
						response.addResult(new CommandResult(ResultStatus.ERROR, "Operation not provided"));
					}
						
					this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
				break;//END of Wireless Mode
			
				case DEVICE:
					
					if( command.getOperation() == null ){
						response.addResult(new CommandResult(ResultStatus.ERROR, "No operation provided..."));
						this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
						return;
					}
					
					if( command.getOperation() == Operation.LIST ){
						List<DeviceDbModel> devicesFnd = this.listDevices();
						if( devicesFnd == null || devicesFnd.size() == 0 ){
							response.addResult(new CommandResult(ResultStatus.EXECUTED, "No devices found on Registry of Gateway..."));
							this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
							return;
						}
						for( DeviceDbModel devObj : devicesFnd ){
							response.addResult(new CommandResult(devObj.getAddress(), 0, devObj.getBattery_level(),devObj.getStatus()));
						}
						this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
						return;
					}
					
					if( command.getIdentities() == null ){
						response.addResult(new CommandResult(ResultStatus.ERROR, "No identities provided..."));
						this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
						return;
					}
					
					//Disconnect operation execution
					/*if( command.getOperation() == Operation.DISCONNECT ){
						for( String deviceAddr: command.getIdentities() ){
							DeviceDbModel deviceObj = this.getDevice(deviceAddr);
							if( deviceObj != null ){
								boolean status = this.wsnHandler.performActionToDevice(deviceObj.getAddress(), command.getOperation());
								if( status ){
									response.addResult(new CommandResult(deviceObj.getAddress(), 0, deviceObj.getBattery_level(),0));
								}else {
									response.addResult(new CommandResult(deviceAddr, ResultStatus.ERROR, "Unable to perform operation(DISCONNECT)..."));
								}
							}else{
								response.addResult(new CommandResult(deviceAddr, ResultStatus.ERROR, "Device is not found...."));
							}
						}
					}else if( command.getOperation() == Operation.CONNECT ){
						for( String deviceAddr: command.getIdentities() ){
							boolean status = this.wsnHandler.performActionToDevice(deviceAddr, command.getOperation());
							if( status ){
								CommandResult curResult = new CommandResult(deviceAddr, 0, 0, 0);
								curResult.setStatus(ResultStatus.EXECUTED);
								response.addResult(curResult);
							}else {
								response.addResult(new CommandResult(deviceAddr, ResultStatus.ERROR, "Unable to perform operation(CONNECT)..."));
							}
						}
					}*/
					this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
					
				break;//END of Device Connect - Disconnect
			
				case PING:
					if( command.getIdentities() == null ){
						response.addResult(new CommandResult(ResultStatus.ERROR, "No identities provided..."));
						this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
						return;
					}
					
					for( String deviceAddr: command.getIdentities() ){
						DeviceDbModel deviceObj = this.getDevice(deviceAddr);
						if( deviceObj != null ){
							String reason = "Device timeout...";
							ResultStatus devStat = ResultStatus.TIMEOUT;
							if( deviceObj.getStatus() == 1 ){
								reason = null;
								devStat = ResultStatus.SUCCESSFUL;
							}
							response.addResult(new CommandResult(deviceObj.getAddress(), devStat, reason));
						}else{
							response.addResult(new CommandResult(deviceAddr, ResultStatus.ERROR, "Device is not found...."));
						}
					}
					this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
				break;//ENDof PING Device.
				
				case ADVERTISEMENT:
					//TODO
					logger.info("Going to execute command Advertisment - Unimplemented");
				break;
				
				case BATTERY_INDICATION:
					if( command.getIdentities() == null ){
						response.addResult(new CommandResult(ResultStatus.ERROR, "No identities provided..."));
						this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
						return;
					}
					
					for( String deviceAddr: command.getIdentities() ){
						DeviceDbModel deviceObj = this.getDevice(deviceAddr);
						if( deviceObj != null ){
							response.addResult(new CommandResult(deviceObj.getAddress(), 0, deviceObj.getBattery_level(), deviceObj.getStatus()));
						}else{
							response.addResult(new CommandResult(deviceAddr, ResultStatus.ERROR, "Device is not found...."));
						}
					}
					this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
				break;//END of Battery Indication
				
				case OPERATING_SYSTEM:
					if( command.getOperation() != null ){
						this.watchDog.applyCommand(command.getType(), command.getOperation());
					}else{
						logger.error("Invalid command message received");
						response.addResult(new CommandResult(ResultStatus.ERROR, "Operation not provided..."));
						this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
					}
				break;//END of Operating System
			
				case RESOURCES:
					Monitoring resources = this.monitoringService.checkSystemResources();
					this.sendResponse(this.jsonMapper.writeValueAsBytes(resources));
				break;//END of Resources
			
				case APPLICATION:
					
					if( command.getOperation() == null ){
						response.addResult(new CommandResult(ResultStatus.ERROR, "Operation not provided..."));
						this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
						return;
					}
					
					if( (command.getOperation() == Operation.START || command.getOperation() == Operation.RESTART || command.getOperation() == Operation.SHUTDOWN ) && (command.getIdentities() == null )){
						response.addResult(new CommandResult(ResultStatus.ERROR, "No identities provided..."));
						this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
						return;
					}
					
					switch( command.getOperation() ){
					
						case LIST:
							ArrayList<Application> applications = this.monitoringService.checkApplications();//TODO Check
							for( Application app : applications ){
								response.addResult(new CommandResult(app.getIdentity(), app.getIp(), app.getPid()));
							}
						break;
				
						case START:
							for( String appname : command.getIdentities() ){
								File runningApp = null;
								String currentName = appname;
								if( !appname.endsWith(".jar") ) currentName = appname + ".jar";
								runningApp = new File(this.appConfig.getAppsRepo() + currentName);
								if( runningApp.exists() ){
									boolean appStatus = runningApp.renameTo(new File(this.appConfig.getAppsDir()+currentName));
									if( appStatus ){
										Thread.sleep(2000);
										Application appInfo = this.monitoringService.getApplication(currentName);
										if(  appInfo != null ) response.addResult(new CommandResult(appInfo.getIdentity(), appInfo.getIp(), appInfo.getPid()));
										else response.addResult(new CommandResult());
									}else response.addResult(new CommandResult(ResultStatus.ERROR, "Unable to start application..."));
								}else{
									response.addResult(new CommandResult(ResultStatus.ERROR, "Application is not exist on repository..."));
								}
							}
						break;
						
						case RESTART:
							for( String appname : command.getIdentities() ){
								File runningApp = null;
								String currentName = appname;
								if( !appname.endsWith(".jar") ) currentName = appname + ".jar";
								runningApp = new File(this.appConfig.getAppsDir() + currentName);
								if( runningApp.exists() ){
									boolean appStatusStop = runningApp.renameTo(new File(this.appConfig.getAppsRepo()+currentName));
									runningApp = new File(this.appConfig.getAppsRepo() + currentName);
									boolean appStatusStart = runningApp.renameTo(new File(this.appConfig.getAppsDir()+currentName));
									if( appStatusStop && appStatusStart ){
										Application appInfo = this.monitoringService.getApplication(currentName);
										Thread.sleep(2000);
										if(  appInfo != null ) response.addResult(new CommandResult(appInfo.getIdentity(), appInfo.getIp(), appInfo.getPid()));
										else response.addResult(new CommandResult());
									}else response.addResult(new CommandResult(ResultStatus.ERROR, "Unable to shutdown application..."));
								}else{
									response.addResult(new CommandResult(ResultStatus.ERROR, "Application is not running..."));
								}
							}
						break;
							
							
						case SHUTDOWN:
							for( String appname : command.getIdentities() ){
								File runningApp = null;
								String currentName = appname;
								if( !appname.endsWith(".jar") ) currentName = appname + ".jar";
								runningApp = new File(this.appConfig.getAppsDir() + currentName);
								if( runningApp.exists() ){
									boolean appStatus = runningApp.renameTo(new File(this.appConfig.getAppsRepo()+currentName));
									if( appStatus ){
										Application appInfo = this.monitoringService.getApplication(currentName);
										if(  appInfo != null ) response.addResult(new CommandResult(appInfo.getIdentity(), appInfo.getIp(), appInfo.getPid()));
										else response.addResult(new CommandResult());
									}else response.addResult(new CommandResult(ResultStatus.ERROR, "Unable to shutdown application..."));
								}else{
									response.addResult(new CommandResult(ResultStatus.ERROR, "Application is not running..."));
								}
							}
						break;
							
						case CHECK:
							response.addResult(new CommandResult(ResultStatus.ERROR, "CHECK operation is not implemented yet"));
						break;
						
						default:
							response.addResult(new CommandResult(ResultStatus.ERROR, "Invalid operation type: " + command.getOperation()));
					
					}				
					this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
				break;//END case APPLICATION
				
				default:
					logger.warn("Unhandled command type: " + command.getType());
			
			}
		} catch (IOException | InterruptedException e) {
			logger.error("Unable to parse Configuration Message",e);
			CommandResponse response = new CommandResponse();
			response.setUuid(UUID.randomUUID().toString());
			response.setType(GwCommandType.UNKNOWN);
			response.setOperation(Operation.UNKNOWN);
			response.addResult(new CommandResult(ResultStatus.ERROR, "Invalid command received..."));
			try {
				this.sendResponse(this.jsonMapper.writeValueAsBytes(response));
			} catch (JsonProcessingException e1) {}
		}
	}
	
	private List<DeviceDbModel> listDevices() {
		ResultSetHandler<List<DeviceDbModel>> rsh = new BeanListHandler<DeviceDbModel>(DeviceDbModel.class);
		List<DeviceDbModel> devicesFnd;
		try {
			devicesFnd = this.database.getSQLRunner().query("SELECT * FROM devices", rsh);
			return devicesFnd;
		} catch (SQLException e) {
			logger.error("Get Devices list failed...",e);
		}
		return null;
	}

	/**
	 * After process the command send the response back.
	 * @param data
	 */
	private void sendResponse(byte[] data){
		this.cloudPublisher.publish(this.SOURCE_IDENTIFIER, data);
	}
	
	/**
	 * Retrieve a device from database.
	 * @param address - The address of the device.
	 * @return
	 */
	private DeviceDbModel getDevice(String address){
		ResultSetHandler<DeviceDbModel> devHandler = new BeanHandler<DeviceDbModel>(DeviceDbModel.class);
		DeviceDbModel device;
		try {
			device = this.database.getSQLRunner().query("SELECT rowid AS id, * FROM devices WHERE address=?", devHandler, address);
			return device;
		} catch (SQLException e) {
			logger.error("Unable to find device...",e);
		}
		return null;
	}

}
