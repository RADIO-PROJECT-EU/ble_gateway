package org.atlas.gateway.components.router;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.atlas.gateway.components.database.Database;
import org.atlas.gateway.components.database.models.Route;
import org.atlas.gateway.services.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouterImpl implements Router{
	
	private static final Logger logger = LoggerFactory.getLogger(RouterImpl.class);
	
	@Autowired
	private Database database;
	
	private HashMap<String, Route> routingTable;
	
	@PostConstruct
	public void bootUpRouter(){
		logger.info("Booting up router...");
		logger.info("Loading routing table");
		try{
			this.routingTable = new HashMap<String, Route>();
			this.loadRoutingTable();
		} catch (IOException e) {
			logger.error("Unable to load routing table");
		}
		logger.info("Router booted up successfully!!");
	}
	
	@Override
	public Route getDestination(String source){
		return this.routingTable.get(source);
	}
	
	/**
	 * Loading the routing table from Database
	 * @param location
	 * @return
	 * @throws IOException
	 */
	private void loadRoutingTable() throws IOException {
		ResultSetHandler<List<Route>> rsh = new BeanListHandler<Route>(Route.class);
		List<Route> routes;
		try {
			routes =  this.database.getSQLRunner().query("SELECT rowid AS id, * FROM routes", rsh);
			for( Route route: routes ){
				this.routingTable.put(route.getSource(), route);
			}
		} catch (SQLException e) {
			logger.error("Routing table load failed...",e);
		}
	}
	
}
