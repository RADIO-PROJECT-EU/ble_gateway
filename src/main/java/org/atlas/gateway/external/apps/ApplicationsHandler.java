package org.atlas.gateway.external.apps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.atlas.gateway.components.database.Database;
import org.atlas.gateway.components.database.models.Application;
import org.atlas.gateway.components.database.models.Information;
import org.atlas.gateway.components.mediator.Mediator;
import org.atlas.gateway.components.mediator.Subscription;
import org.atlas.gateway.components.mediator.processors.Processor;
import org.atlas.gateway.configurations.ApplicationsConfig;
import org.atlas.gateway.connectors.AtlasConnection;
import org.atlas.gateway.connectors.AtlasConnectionFactory;
import org.atlas.gateway.connectors.SysLocalConnector;
import org.atlas.gateway.external.apps.conditions.IsLinuxEnvironmentCondition;
import org.atlas.gateway.external.apps.messages.ConfigMsg;
import org.atlas.gateway.services.CloudPublisher;
import org.atlas.gateway.utils.OsUtils;
import org.atlas.gateway.utils.OsUtils.OSType;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Conditional(IsLinuxEnvironmentCondition.class)
public class ApplicationsHandler implements MqttCallback{
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationsHandler.class);
	private ObjectMapper jsonMapper = new ObjectMapper();
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private boolean applicationsBootingFinished = false;
	private AtlasConnection localConnection;
	
	@Autowired
	private Mediator mediator;
	
	@Autowired
	private ApplicationsConfig config;
	
	@Autowired
	private Database database;
	
	@Autowired
	private CloudPublisher cloudPublisher;
	
	@PostConstruct
	public void bootUpApplications(){
		logger.info("Booting up applications handler");
		if( OsUtils.getOperatingSystemType() != OSType.Linux ){
			logger.warn("Current Gateway version support auto-start functioanlity on Linux only");
			return;
		}
		
		if( config.isAppsAutoStartEnabled() ){
			this.startApplications();
			new ApplicationsWatcher(this, Paths.get(config.getAppsDir()));
		}
		this.connectToLocalMediator();
		this.applicationsBootingFinished = true;
		logger.info("Applications Handler booted up successfully...");
	}
	
	private void connectToLocalMediator() {
		this.localConnection = AtlasConnectionFactory.createConnection("tcp://127.0.0.1:1883", "Local-Client-ApplicationHandler");
		this.localConnection.setListener(this);
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName("application");
		options.setPassword("@PpL3c@tI0n".toCharArray());
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		this.localConnection.connect(options);
		this.localConnection.addSubscription(new Subscription("apps/notifications"));
		this.localConnection.addSubscription(new Subscription("apps/alerts"));
		this.localConnection.addSubscription(new Subscription(this.config.getConfigurationsTopic()));
	}

	@PreDestroy
	public void cleanUpApplications(){
		try{
			List<Application> apps = this.getApplications();
			for( Application enabledApp : apps ){
				if( enabledApp.getPid() > 0 ) killProcess(enabledApp.getPid());
				this.deleteApplicationRegistry(enabledApp);
			}
		} catch (Exception e) {
        	logger.error("Unable to stop running applications on ", e);
        }		
	}
	
	/**
	 * Kill an existing process
	 * @param pid - The Process ID to kill
	 * @throws IOException 
	 */
	public void killProcess(int pid) throws IOException{
		logger.info("Killing process ("+pid+")");
		Runtime.getRuntime().exec("kill -15 " + pid);
		logger.info("Application ("+pid+") stopped successfully");
	}
	
	/**
	 * List and start the applications
	 */
	private void startApplications(){
		File folder = new File(config.getAppsDir());
		
		System.out.println(config.getAppsDir());
		
		if( !folder.exists() ){
			logger.warn("Unable to find folder("+config.getAppsDir()+") that contains the applications");
			return;
		}
		
		File[] filesList = folder.listFiles();
		for (File file : filesList) {
	      if ((file.isFile()) && (file.getName().endsWith(".jar"))) { ////TODO Not checking the .jar as extension
	    	  ApplicationLoader application = this.bootApp(file.getAbsolutePath());
	    	  if( application != null ){
	    		  this.addApplicationToRegistry(application);
	    	  }else{
	    		  logger.warn("Application ("+file.getName()+") not started successfully, not pid provided");
	    	  }
	      }
	    }
	}
	
	private void deleteApplicationRegistry(Application application) throws SQLException{
		application.setIp("-");
		application.setStatus(2);
		this.updateApplcation(application);
	}
	
	private void addApplicationToRegistry(ApplicationLoader application) {
		boolean update = false;
		try{
			Application checkApp = this.getApplicationByName(application.getName());
			if( checkApp != null ) update = true;
			else checkApp = new Application();
			checkApp.setIdentity(application.getName());
			checkApp.setIp("localhost");
			checkApp.setPid(application.getPid());
			checkApp.setStatus(1);
			if(update){
				this.updateApplcation(checkApp);
			}else{
				this.addApplcation(checkApp);
			}
		}catch(SQLException e){
			 logger.error("Unable to add application to registry",e);
		}
	}

	/**
	 * Start the application on the Given Path
	 * @param path
	 * @return
	 */
	public ApplicationLoader bootApp(String path){
		try {
			return this.executor.submit(new ApplicationLoader(path, config.getAppLoaderDirectory())).get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Unable to submit application for starting up..",e);
		}
		return null;
	}
	
	public void restartApplication(String appName){
		Application checkApp = null;
		try {
			checkApp = this.getApplicationByName(appName);
		} catch (SQLException e) {
			logger.error("Unable to get application info from registry..",e);
		}
		String absoluteFilename = config.getAppsDir()+appName;
		ApplicationLoader application = null;
		if( checkApp == null ){
			logger.info("Application not exists on registry, booting up Application");
			application = this.bootApp(absoluteFilename);
		}else{
			logger.info("Application already exists on registry, restarting Application");
			if( isProcessExist(checkApp.getPid()) ){
				logger.info("Application already running with pid ("+checkApp.getPid()+"), shutting down application");
				this.shutdownApp(appName);
			}
			application = this.bootApp(absoluteFilename);
		}
		
		if( application != null ) this.addApplicationToRegistry(application);
		else logger.warn("Application ("+appName+") not started successfully, not pid provided");	
	}
	
	public boolean isProcessExist(int pid){
		File f = new File("/proc/"+pid+"/exe");
		return f.exists();
	}
	
	/**
	 * Shutdown an application by Application Name
	 * @param pid
	 */
	public void shutdownApp(String appName){
		logger.info("Going to kill application ("+appName+")");
		logger.info("Checking application ("+appName+") to registry");
		try {
			Application curApp = this.getApplicationByName(appName);
			 if( curApp != null && curApp.getPid() > 0 ){
				logger.info("Application found to registry with PID ("+curApp.getPid()+")..");
				killProcess(curApp.getPid());
			 	this.deleteApplicationRegistry(curApp);
			}else{
				logger.info("Application not found to registry...");
			}
		} catch (IOException | SQLException e) {
			logger.error("Unable to kill application..",e);
		}
	}
	
	@Scheduled(fixedDelayString = "${atlas.gw.apps.check.interval}")
    public void appsRunningChecker() {
		
		if( !config.isAppsAutoStartEnabled() ){
			logger.debug("Application auto start not enabled...");
			return;
		}
		
		if( !this.applicationsBootingFinished ){
			logger.warn("Applications initialization not finished yet, waiting to finish the initialization");
			return;
		}
		
		File folder = new File(config.getAppsDir());
		
		if( !folder.exists() ){
			logger.warn("Unable to find folder("+config.getAppsDir()+") that contains the applications");
			return;
		}
		
		try {
			File[] filesList = folder.listFiles();
			for (File file : filesList) {
				ApplicationLoader application = null;
				if ((file.isFile()) && (file.getName().endsWith(".jar"))) {//TODO Not checking the .jar as extension 
					logger.debug("Checking application ("+file.getName()+") to registry");
					Application curApp = this.getApplicationByName(file.getName());
					 if( curApp != null && curApp.getPid() > 0 ){
						 logger.debug("Application exist in registry..Checking process PID: " + curApp.getPid());
						 if( !this.isProcessExist(curApp.getPid()) ){
							 logger.debug("Application might closed unexpectetly...");
							 application = this.bootApp(file.getAbsolutePath());
						 }
					 }else{
						 application = this.bootApp(file.getAbsolutePath());
					 }
				}
				if( application != null ) this.addApplicationToRegistry(application);
			}
		}catch (SQLException e) {
			logger.error("Unabl to get application info from registry",e);
		}
		
    }
	
	/**
	 * Retrieve all the applications
	 * @return List of applications
	 */
	private List<Application> getApplications(){
		ResultSetHandler<List<Application>> rsh = new BeanListHandler<Application>(Application.class);
		List<Application> applications;
		try {
			applications = this.database.getSQLRunner().query("SELECT rowid AS id, * FROM applications", rsh);
			return applications;
		} catch (SQLException e) {
			logger.error("Get Applicastions failed...",e);
		}
		return null;
	}
	
	/**
	 * Get an application by Name
	 * @param appName - The application name to search
	 * @return Application
	 * @throws SQLException
	 */
	private Application getApplicationByName(String appName) throws SQLException{
		ResultSetHandler<Application> h = new BeanHandler<Application>(Application.class);
		Application app = this.database.getSQLRunner().query("SELECT rowid AS id,* FROM applications WHERE identity=? LIMIT 1", h, appName);
		return app;
	}
	
	/**
	 * Retrieve a configuration
	 */
	private String getAppsConfiguration(String configuration) throws SQLException{
		ResultSetHandler<Information> infoHandler = new BeanHandler<Information>(Information.class);
		Information info;
		try {
			info = this.database.getSQLRunner().query("SELECT * FROM gwinfo WHERE configId=?", infoHandler, configuration);
			return info.getConfigValue();
		} catch (SQLException e) {
			logger.error("Unable to retrieve public IP from database",e);
			return null;
		}
	}
	
	
	/**
	 * Add new application
	 */
	private void addApplcation(Application app) throws SQLException{
		this.database.getSQLRunner().update( "INSERT INTO applications (identity,createdAt,updatedAt,ip,pid,status) VALUES (?,datetime(),datetime(),?,?,?)", app.getIdentity(), app.getIp(), app.getPid(), app.getStatus());		
	}
	
	/**
	 * Update application
	 */
	private void updateApplcation(Application app) throws SQLException{
		this.database.getSQLRunner().update( "UPDATE applications SET identity=?, updatedAt=datetime(), ip=?, pid=?, status=? WHERE rowid=?", app.getIdentity(), app.getIp(), app.getPid(), app.getStatus(), app.getId());
	}

	@Override
	public void connectionLost(Throwable cause) {}

	//TODO
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		if( topic.equals(this.config.getConfigurationsTopic()) ){
			ConfigMsg msg = this.jsonMapper.readValue(message.getPayload(), ConfigMsg.class);
			logger.info("Configuration message received from application: " + msg.getApplication());
			String config = this.getAppsConfiguration(msg.getConfig());
			if( config == null ) {
				logger.warn("Unable to found configuration: " + msg.getConfig());
				msg.setFound(false);
			}else {
				logger.info("Configuration found with value: " + msg.getConfigdata());
				msg.setFound(true);
				msg.setConfigdata(config);
			}
			SysLocalConnector.publish(msg.getResponseTopic(), this.jsonMapper.writeValueAsBytes(msg), 2);
		}else{
			this.cloudPublisher.publish(topic, message.getPayload());
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {}
	
}
