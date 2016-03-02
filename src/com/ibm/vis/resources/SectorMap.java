/*
 * SectorMap.java
 * 
 * (C) 2016 IBM India Pvt. Ltd.
 * All Rights Reserved.
 * 
 * This program is a part of the VisitorInformationManagement System.
 * 
 */
package com.ibm.vis.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ibm.vis.utils.GlobalConsts;

/**
 * This is a utility service to retrieve the sector-industry mapping as configured
 * in the Bluemix environment variables.
 * @author <a href="mailto:siddhartha.ghosh@in.ibm.com">Siddhartha Ghosh</a>
 *
 */
@Path("/sectormap")
public class SectorMap {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSectorMap() {
		String sectorMapStr = System.getenv(GlobalConsts.ENV_SECTOR_INDUSTRY_MAP);
		return sectorMapStr;
	}
}
