/*
 * CloudDBUtil.java
 * (C) 2016 Siddhartha Ghosh / IBM India Pvt. Ltd.
 * All Rights Reserved.
 */
package com.ibm.vis.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

/**
 * This class is designed to replace the DBUtil class 
 * and is targeted to be used with the Cloudant DB service on Bluemix.
 * @author Siddhartha Ghosh
 *
 */
public class CloudDBUtil {
	private static CloudDBUtil self;
	protected Logger logger;
	protected CloudantClient client;
	
	private CloudDBUtil() {
		logger = Logger.getLogger(CloudDBUtil.class.getName());
	}
	
	public CloudantClient getClient() throws MalformedURLException {
		if ( client == null ) {
			logger.debug("initializing cloudant client and connection...");
			logger.debug("reading configuration...");
			String vcap = System.getenv("VCAP_SERVICES");
			if ( vcap == null ) {
				logger.error("could not find VCAP variable");
				throw new IllegalStateException("VCAP env var not defined");
			} else {
				logger.info("VCAP found");
			}

			JSONObject serviceObj = new JSONObject(vcap);
			JSONObject creds = serviceObj.getJSONArray("cloudantNoSQLDB")
					.getJSONObject(0)
					.getJSONObject("credentials");
			String cloudantURL = creds.getString("url");
			String userid = creds.getString("username");
			String password = creds.getString("password");
			
			logger.debug("creating cloudant client...");
			client = ClientBuilder.url(new URL(cloudantURL))
					.username(userid)
					.password(password)
					.build();
			logger.info("connected to cloudant couchdb server version " 
					+ client.serverVersion());
		}
		
		return client;
	}
	
	public Database getDB(String dbName, boolean createFlag) throws MalformedURLException {
		return getClient().database(dbName, createFlag);
	}
	
	public static final CloudDBUtil createInstance() {
		if ( self == null ) {
			self = new CloudDBUtil();
		}
		
		return self;
	}
}
