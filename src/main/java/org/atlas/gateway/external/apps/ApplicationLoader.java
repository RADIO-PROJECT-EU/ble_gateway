package org.atlas.gateway.external.apps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLoader implements Callable<ApplicationLoader> {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationLoader.class);
	
	private String path;
	private String name;
	private String loader;

	private int pid = 0;
	
	public ApplicationLoader(String path, String loader){
		this.path = path;
		this.name = this.extractAppName(path);
		this.loader = loader;
		logger.info("Starting application on " + this.getPath());
	}

	@Override
	public ApplicationLoader call() throws Exception {
		Process p;
        String s;
        try {
        	p = Runtime.getRuntime().exec(this.getLoader() + " " + this.getName());         
            BufferedReader br = new BufferedReader( new InputStreamReader(p.getInputStream()));
            if ((s = br.readLine()) != null) {
            	this.pid = Integer.parseInt(s);
            }
            logger.info("Application started successfully with pid: " + this.pid);
        } catch (Exception e) {
        	logger.error("Unable to start application on " + this.getPath(), e);
        }
        return this;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public String getLoader() {
		return loader;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}
	
	private String extractAppName(String appPath) {
		String appname = "";
		String temp[] = appPath.split("/");
		appname = temp[temp.length-1];
 		return appname;
	}

}
