package org.atlas.gateway.components.watchers;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.atlas.gateway.components.database.Database;
import org.atlas.gateway.components.database.models.Application;
import org.atlas.gateway.components.database.models.Information;
import org.atlas.gateway.configurations.GatewayConfig;
import org.atlas.gateway.services.MonitoringService;
import org.atlas.gateway.supervisor.MemoryType;
import org.atlas.gateway.supervisor.Monitoring;
import org.atlas.gateway.supervisor.Partition;
import org.atlas.gateway.supervisor.SysCpus;
import org.atlas.gateway.supervisor.SysGeneral;
import org.atlas.gateway.supervisor.SysInfo;
import org.atlas.gateway.supervisor.SysMemory;
import org.atlas.gateway.supervisor.SysTasks;
import org.atlas.gateway.supervisor.SystemLoad;
import org.atlas.gateway.supervisor.TopInfo;
import org.atlas.gateway.utils.IpAddrUtils;
import org.atlas.gateway.utils.OsUtils;
import org.atlas.gateway.utils.OsUtils.OSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonitoringServiceImpl implements MonitoringService {
	
	private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
	
	private OSType ostype;
	
	@Autowired
	private GatewayConfig gwConfig;
	
	@Autowired
	private Database database;
	
	@PostConstruct
	public void startResourcesService(){
		logger.info("Initializing resources service.");
		this.ostype = OsUtils.getOperatingSystemType();
	}

	@Override
	public Monitoring checkSystemResources() {
		Monitoring monitor = new Monitoring();
		SysInfo sysInfo = new SysInfo();
		sysInfo.setCores(Runtime.getRuntime().availableProcessors());		
		sysInfo.setOsName(System.getProperty("os.name"));
		sysInfo.setOsVersion(System.getProperty("os.version"));
		sysInfo.setOsArch(System.getProperty("os.arch"));
		sysInfo.setIpAddrs(IpAddrUtils.getLocalIpAddressToArray());
		sysInfo.setPublicIp(gwConfig.getPublicIp());
		
		if( this.ostype == OSType.Linux ){
			monitor.setMemories(OsUtils.getOperatingSystemMemory());			
			List<TopInfo> informations = OsUtils.getOsInfo();
			for( TopInfo info: informations ){
				switch( info.getInfoType() ){
					case GENERAL:
						SysGeneral general = (SysGeneral) info;
						sysInfo.setLocalTime(LocalDate.now() + " " + general.getOsTime());
						sysInfo.setUpTime(general.getUpTime());
						sysInfo.setSysLoad(new SystemLoad(general.getLastMinuteLoadAverage(), general.getLastFiveMinutesLoadAverage(), general.getLastFifteenMinutesLoadAverage()));
					break;
					case TASKS:
						SysTasks tasks = (SysTasks) info;
						sysInfo.setTasksInfo(tasks);
					break;
					case CPUS:
						SysCpus cpus = (SysCpus) info;
						sysInfo.setCpusInfo(cpus);
					break;
				}
			}
		}
		
		SysMemory jvmMem = new SysMemory(MemoryType.VIRTUAL);
		jvmMem.setTotal(Runtime.getRuntime().maxMemory());
		jvmMem.setFree(Runtime.getRuntime().freeMemory());
		jvmMem.setAvailable(Runtime.getRuntime().totalMemory());	
		monitor.addMemory(jvmMem);
		File[] roots = File.listRoots();
	    for (File root : roots) {
	    	monitor.addPartition(new Partition(root.getAbsolutePath(), root.getTotalSpace(), root.getFreeSpace(), root.getUsableSpace()));
	    }
	    monitor.setSysInfo(sysInfo);
	    return monitor;
	}

	@Override
	public ArrayList<Application> checkApplications() {
		ResultSetHandler<List<Application>> rsh = new BeanListHandler<Application>(Application.class);
		List<Application> applications;
		ArrayList<Application> appsList = new ArrayList<Application>();
		try {
			applications =  this.database.getSQLRunner().query("SELECT rowid AS id, * FROM applications", rsh);
			for( Application app: applications ){
				appsList.add(app);
			}
		} catch (SQLException e) {
			logger.error("Applications table load failed...",e);
		}
		return appsList;
	}

	@Override
	public Application getApplication(String name) {
		ResultSetHandler<Application> appHandler = new BeanHandler<Application>(Application.class);
		Application app;
		try {
			app = this.database.getSQLRunner().query("SELECT rowid AS id, * FROM applications WHERE identity=?", appHandler, name);
			return app;
		} catch (SQLException e) {
			logger.error("Unable to find application...",e);
		}
		return null;
	}

}
