package org.atlas.gateway.configurations;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.atlas.gateway.components.database.Database;
import org.atlas.gateway.components.database.models.Information;
import org.atlas.gateway.utils.IpAddrUtils;
import org.atlas.gateway.utils.OsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
/**
 * Main Gateway configurations
 */
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
	
	private static final Logger logger = LogManager.getLogger(GatewayConfig.class);
	
	@Autowired
	private Database database;

	@PostConstruct
	public void initGwConfigurations(){
		String publicIpAddress = IpAddrUtils.getExternalIPAddress();
		logger.info("Checking gateway IP Address: " + publicIpAddress);
		try {
			this.addInformation(new Information("pid", this.getCurrentProgramProcess()));
			this.addInformation(new Information("os", OsUtils.getOperatingSystemType().toString()));
			this.addInformation(new Information("public_ip", publicIpAddress));			
		} catch (SQLException e) {
			logger.info("Unable to save information to database",e);
		}
		
	}
	
	@Value("${atlas.gw.identity}")
	private String identity;
	
	@Value("${atlas.gw.commands.topic}")
	private String commandsTopic;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	/**
	 * Store gateway information
	 */
	public void addInformation(Information info) throws SQLException{
		this.database.getSQLRunner().update( "INSERT INTO gwinfo (configId,configValue,createdAt,updatedAt) VALUES (?,?,datetime(),datetime())", info.getConfigId(), info.getConfigValue());		
	}
	
	/**
	 * Retrieve from the database the Gateway Public IP Address.
	 * @return
	 */
	public String getPublicIp(){
		ResultSetHandler<Information> infoHandler = new BeanHandler<Information>(Information.class);
		Information info;
		try {
			info = this.database.getSQLRunner().query("SELECT * FROM gwinfo WHERE configId=?", infoHandler, "public_ip");
			return info.getConfigValue();
		} catch (SQLException e) {
			logger.error("Unable to retrieve public IP from database",e);
		}
		return null;
	}

	public String getCommandsTopic() {
		return commandsTopic.replace("{gateway-identity}", this.getIdentity());
	}

	public void setCommandsTopic(String commandsTopic) {
		this.commandsTopic = commandsTopic;
	}
	
	/**
	 * Find the current running process.
	 * @return
	 */
	private String getCurrentProgramProcess(){
		String[] temp = ManagementFactory.getRuntimeMXBean().getName().split("@");
		return temp[0];
	}
	
}
