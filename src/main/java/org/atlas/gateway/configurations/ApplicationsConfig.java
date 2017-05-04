package org.atlas.gateway.configurations;

import java.util.ArrayList;
import java.util.Arrays;

import org.atlas.gateway.utils.OsUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationsConfig {
	
	private final String APPLICATIONS_CONFIGURATIONS_TOPIC = "apps/configurations";
	private final String GATEWAY_HOME  = OsUtils.getEnviromentalVariable("GATEWAY_HOME");

	@Value("${atlas.gw.apps.directory}")
	private String appsDir;
	
	@Value("${atlas.gw.apps.repo}")
	private String appsRepo;
	
	@Value("${atlas.gw.apps.autostart:true}")
	private boolean appsAutoStartEnabled;
	
	@Value("${atlas.gw.apps.exclude:}")
	private String excludedApplications;
	
	@Value("${atlas.gw.apps.scripts.directory}")
	private String appLoaderDirectory;
	
	public String getAppLoaderDirectory() {
		return this.GATEWAY_HOME+"/"+appLoaderDirectory;
	}

	public void setAppLoaderDirectory(String appLoaderDirectory) {
		this.appLoaderDirectory = appLoaderDirectory;
	}

	private ArrayList<String> excludedAppsList;
	
	public String getAppsDir() {
		return this.GATEWAY_HOME+"/"+appsDir;
	}

	public void setAppsDir(String appsDir) {
		this.appsDir = appsDir;
	}

	public boolean isAppsAutoStartEnabled() {
		return appsAutoStartEnabled;
	}

	public void setAppsAutoStartEnabled(boolean appsAutoStartEnabled) {
		this.appsAutoStartEnabled = appsAutoStartEnabled;
	}
	
	public String getExcludedApplications() {
		return excludedApplications;
	}

	public void setExcludedApplications(String excludedApplications) {
		this.excludedApplications = excludedApplications;
		this.excludedAppsList = new ArrayList<String>( Arrays.asList( this.excludedApplications.split(",") ) );
	}
	
	public boolean isOnExcludedList(String app){
		return this.excludedAppsList.contains(app);
	}
	
	public String getConfigurationsTopic(){
		return this.APPLICATIONS_CONFIGURATIONS_TOPIC;
	}

	public String getAppsRepo() {
		return this.GATEWAY_HOME+"/"+appsRepo;
	}

	public void setAppsRepo(String appsRepo) {
		this.appsRepo = appsRepo;
	}
	
}
