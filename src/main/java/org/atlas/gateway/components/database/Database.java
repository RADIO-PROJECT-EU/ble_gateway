package org.atlas.gateway.components.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.atlas.gateway.configurations.DatabaseConfig;
import org.atlas.gateway.configurations.GatewayConfig;
import org.atlas.gateway.utils.OsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=1)
public class Database {
	
	private static final Logger logger = LoggerFactory.getLogger(Database.class);
	private final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
	
	@Autowired
	private DatabaseConfig config;
	
	private BasicDataSource datasource;
	private QueryRunner queryRunner;
	
	@PostConstruct
	public void bootUpDatabase(){
		
		logger.info("Booting up Database...");
		initDbDirectory();
		try {
			this.datasource = new BasicDataSource();
			this.datasource.setDriverClassName("org.sqlite.JDBC");
			this.datasource.setUrl(this.SQLITE_JDBC_PREFIX+OsUtils.getEnviromentalVariable("GATEWAY_HOME")+"/"+this.config.getDbUrl());
			this.datasource.getConnection().setAutoCommit(this.config.isAutoCommit());	
			this.queryRunner = new QueryRunner(this.datasource);
		}catch (SQLException e) {
			logger.error("Unable to create connectio with Database",e);
		}
		constructDb();
		logger.info("Database booted successfullly...");
	}
	
	@PreDestroy
	public void shutdownDatabase(){
		logger.info("Shutting down database...");
		try {
			this.datasource.getConnection().close();
		} catch (SQLException e) {
			logger.error("Unable to close connection with database",e);
		}
	}
	
	/**
	 * Create DB directory
	 **/
	private void initDbDirectory(){
		logger.info("Initialize DB directory...");
		Path path = Paths.get(OsUtils.getEnviromentalVariable("GATEWAY_HOME")+"/db/");
		if( !Files.isDirectory(path) ){
			logger.info("DB directory not exist, try to create it...");
			try {
				Files.createDirectory(path);
				logger.info("DB directory created successfully...");
			} catch (IOException e) {
				logger.error("Unable to create DB directory",e);
			}
		}else{
			logger.info("DB directory already exists, skipping...");
		}
	}
	
	/**
	 * Construct database tables
	 **/
	private void constructDb(){		
		try {
			FileInputStream fstream = new FileInputStream(OsUtils.getEnviromentalVariable("GATEWAY_HOME")+"/"+config.getDbSchema());
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			String sql = "";
			while ((line = br.readLine()) != null)   {
				sql += line;
			}
			sql = sql.replaceAll("(--.*)|(((/\\*)+?[\\w\\W]+?(\\*/)+))", "");
			br.close();			
			Statement stmt = this.datasource.getConnection().createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		}catch (IOException e) {
			logger.error("Unable to create construct DB from SQL",e);
		} catch (SQLException e) {
			logger.error("Unable to execute DB construction query",e);
		}
	}
	
	public QueryRunner getSQLRunner(){
		return this.queryRunner;
	}	
}


