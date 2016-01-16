/*
 * TestCloudant.java
 * (C) 2016 Siddhartha Ghosh / IBM India Pvt. Ltd.
 * All Rights Reserved
 */

package com.ibm.vis.resources;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.json.JSONObject;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;

/**
 * The purpose of this class is to test out the Cloudant NoSQL database in BlueMix
 * @author Siddhartha Ghosh (siddhartha.ghosh@in.ibm.com)
 *
 */
@Path("/testcloudant")
public class TestCloudant {
	@GET
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
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			messages.append("[FAILED]\n");
			messages.append("encountered error: " + e.getMessage());
		} 
		
		return messages.toString();
	}
}
