package com.ibm.vis.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

@Path("/visit")
public class VisitResource {
	
	/**
	 * This method is used to create new Visit record instances.
	 * @param visitTypeChoice
	 * @param industry
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String createVisit(
			@FormParam(value="visitTypeChoice") String visitTypeChoice,
			@FormParam(value="industry") String industry,
			@FormParam(value="accName") String accName,
			@FormParam(value="palLFE") String palLFE,
			@FormParam(value="cbc") String cbc) {
		JSONObject retObj = new JSONObject();
		
		retObj.append("visitTypeChoice", visitTypeChoice);
		retObj.append("industry", industry);
		retObj.append("accName", accName);
		retObj.append("palLFE", palLFE);
		retObj.append("cbc", cbc);
		
//		retObj.append("", );
		
		
		return retObj.toString();
	}
}
