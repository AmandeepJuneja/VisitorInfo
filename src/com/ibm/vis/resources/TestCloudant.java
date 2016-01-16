/*
 * TestCloudant.java
 * (C) 2016 Siddhartha Ghosh / IBM India Pvt. Ltd.
 * All Rights Reserved
 */

package com.ibm.vis.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
		
		messages.append("testing cloudant...");
		return messages.toString();
	}
}
