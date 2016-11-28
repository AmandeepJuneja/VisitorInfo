/*
 * DBUtil.java
 * (C) 2016 Siddhartha Ghosh / IBM India Pvt. Ltd.
 * All Rights Reserved.
 */
package com.ibm.vis.utils;

import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

/**
 * This is a database utility wrapper. 
 * @author Siddhartha Ghosh (siddhartha.ghosh@in.ibm.com)
 *
 */
public class DBUtil {
	
	private static DBUtil singletonMe;
	private static Logger logger = Logger.getLogger(DBUtil.class.getName());
	
	private DB db;
	private Mongo client;
	
	public static DBUtil createInstance() throws IllegalStateException{
		if (singletonMe == null) {
			singletonMe = new DBUtil();
			logger.info("Creating DBUtil instance for cloudorr...");
			
			try {
				JSONObject serviceObj = Global.getVCAP();
								
				String mongoUriTxt = serviceObj.getJSONArray("mongolab").getJSONObject(0).getJSONObject("credentials").getString("uri");
				
				if ( mongoUriTxt == null || mongoUriTxt.length() <= 0 ) {
					throw new IllegalStateException("MongoDB service not configured.");
				}
				
				logger.info("All parameters collected. Creating instance now...");
				
				MongoURI uri = new MongoURI(mongoUriTxt);
				
				singletonMe.client = new Mongo(uri);
				singletonMe.db = singletonMe.client.getDB(uri.getDatabase());
				
				if ( singletonMe.db.authenticate(Global.MDB_USER_ID, Global.MDB_PASSWORD.toCharArray()) ) {
					logger.info("Authenticated successfully.");
				} else { 
					throw new IllegalStateException("Failed to authenticate");
				}
				
				logger.info("Connected to MongoDB on " + uri.toString());
				
				/*if ( singletonMe.db.authenticate(username, password.toCharArray()) ) {
					logger.info("Authenticated successfully.");
				} else { 
					throw new IllegalStateException("Failed to authenticate");
				}*/
				
			} catch (Exception e) {
				throw new IllegalStateException("Failed to create db instance", e);
			} 
			
		}
		
		return singletonMe;
	}

	public DB getDb() {
		return db;
	}

	public Mongo getClient() {
		return client;
	}
	
	public DBCollection getCollection(String collection) {
		return db.getCollection(collection);
	}
}