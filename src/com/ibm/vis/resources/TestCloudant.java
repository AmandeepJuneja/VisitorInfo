/*
 * TestCloudant.java
 * (C) 2016 Siddhartha Ghosh / IBM India Pvt. Ltd.
 * All Rights Reserved
 */

package com.ibm.vis.resources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.DbInfo;

/**
 * The purpose of this class is to test out the Cloudant NoSQL database in BlueMix
 * @author Siddhartha Ghosh (siddhartha.ghosh@in.ibm.com)
 *
 */
@Path("/testcloudant")
public class TestCloudant {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String helloCloudant() {
		StringBuffer messages = new StringBuffer();
		messages.append("reading configurations...");
		
		String vcap = System.getenv("VCAP_SERVICES");
		if ( vcap == null ) {
			messages.append("[FAILED]\n");
			messages.append("null or empty configurations");
			return messages.toString();
		} else {
			messages.append("[OK]\n");
		}

		
		JSONObject serviceObj = new JSONObject(vcap);
		String cloudantURL = serviceObj.getJSONArray("cloudantNoSQLDB").getJSONObject(0).getJSONObject("credentials").getString("url");
		String userid = serviceObj.getJSONArray("cloudantNoSQLDB").getJSONObject(0).getJSONObject("credentials").getString("username");
		String password = serviceObj.getJSONArray("cloudantNoSQLDB").getJSONObject(0).getJSONObject("credentials").getString("password");
		
		messages.append("creating cloudant client...");
		
		// Create a new CloudantClient instance for account endpoint example.cloudant.com
		try {
			CloudantClient client = ClientBuilder.url(new URL(cloudantURL))
					.username(userid)
					.password(password)
					.build();
			messages.append("[OK]\n");
			messages.append("connected to cloudant couchdb server version " + client.serverVersion());
			
			// Get a List of all the databases this Cloudant account
			messages.append("reading databases...\n");
			List<String> databases = client.getAllDbs();
			messages.append("[OK]\n");
			for ( String database : databases ) {
				messages.append(database + " ");
			}
			messages.append("\n");
			
			// Get a Database instance to interact with, but don't create it if it doesn't already exist
			messages.append("getting database instance information...");
			Database db = client.database("vis", false);
			DbInfo dbInfo = db.info();
			messages.append("[OK]\n");
			messages.append(dbInfo + "\n");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			messages.append("[FAILED]\n");
			messages.append("encountered error: " + e.getMessage());
		} 
		
		return messages.toString();
	}
}
