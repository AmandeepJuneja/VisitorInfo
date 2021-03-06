/*
 * CloudVisit.java
 * 
 * (C) 2016 IBM India Pvt. Ltd.
 * All Rights Reserved.
 * 
 * This program is a part of the VisitorInformationManagement System.
 * 
 */
package com.ibm.vis.resources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.views.Key;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.vis.utils.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * This is the new Cloudant based Visit handler resource..
 * @author <a href="mailto:siddhartha.ghosh@in.ibm.com">Siddhartha Ghosh</a>
 *
 */
@Path("/cloudvisit")
public class CloudVisit {
	protected Logger logger = Logger.getLogger(CloudVisit.class.getName());
	protected Database database;
	
	public CloudVisit() throws MalformedURLException {
		super();
		database = CloudDBUtil.createInstance()
				.getDB(Global.CLOUDANT_DB_NAME, false);
	}
	
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
			@Context HttpServletRequest req,
			@FormParam(value="visitTypeChoice") String visitTypeChoice,
			@FormParam(value="industry") String industry,
			@FormParam(value="sector") String sector,
			@FormParam(value="accName") String accName,
			@FormParam(value="palLFE") String palLFE,
			@FormParam(value="cbc") String cbc,
			@FormParam(value="hostMgr") String hostMgr,
			@FormParam(value="visitAgenda") String visitAgenda,
			@FormParam(value="visitorName") List<String> visitorNames,
			@FormParam(value="visitorRole") List<String> visitorRoles,
			@FormParam(value="visitorPrimary") List<String> visitorPrimaries,
			@FormParam(value="itiLoc") List<String> itiLocations,
			@FormParam(value="itiStart") List<String> itiStartDates,
			@FormParam(value="itiEnd") List<String> itiEndDates,
			@FormParam(value="ldrLNID") List<String> ldrNotesIds,
			@FormParam(value="ldrBU") List<String> ldrBUs,
			@FormParam(value="ldrAttnd") List<String> ldrAttnds,
			@FormParam(value="ldrLoc") List<String> ldrLocs,
			@FormParam(value="ldrDate") List<String> ldrDates,
			@FormParam(value="execOwnerTCV") String execOwnerTCV,
			@FormParam(value="opportunityTCV") String opportunityTCV,
			@FormParam(value="deliveryTypeChoice") String deliveryTypeChoice) {
		
		JsonObject visitObj = new JsonObject();
		
		visitObj.addProperty(Global.DOC_ID, IdGenerator.nextVisitId());
		visitObj.addProperty(Global.DOC_TYPE, Global.VISIT_RECORD_TYPE);
		visitObj.addProperty("lastUpdatedBy", req.getRemoteUser());
		visitObj.addProperty("createdBy", req.getRemoteUser());
		visitObj.addProperty("visitTypeChoice", visitTypeChoice);
		visitObj.addProperty("industry", industry);
		visitObj.addProperty("sector", sector);
		visitObj.addProperty("accName", accName);
		visitObj.addProperty("palLFE", palLFE);
		visitObj.addProperty("cbc", cbc);
		visitObj.addProperty("hostMgr", hostMgr);
		visitObj.addProperty("visitAgenda", visitAgenda);
		visitObj.addProperty("execOwnerTCV", execOwnerTCV);
		visitObj.addProperty("opportunityTCV", opportunityTCV);
		visitObj.addProperty("deliveryTypeChoice", deliveryTypeChoice);
		
		JsonArray visitorRecords = new JsonArray();
		for ( int i = 0; i < visitorNames.size(); i++ ) {
			JsonObject visitor = new JsonObject();
			visitor.addProperty("visitorName", visitorNames.get(i));
			visitor.addProperty("visitorRole", visitorRoles.get(i));
			visitor.addProperty("visitorPrimary", visitorPrimaries.get(i));
			
			visitorRecords.add(visitor);
		}
		
		visitObj.add("visitorRecords", visitorRecords);
		
		JsonArray itineraryRecords = new JsonArray();
		for ( int i = 0; i < itiLocations.size(); i++ ) {
			JsonObject itinerary = new JsonObject();
			itinerary.addProperty("itiLoc", itiLocations.get(i));
			itinerary.addProperty("itiStart", itiStartDates.get(i));
			itinerary.addProperty("itiEnd", itiEndDates.get(i));
			itineraryRecords.add(itinerary);
		}
		
		visitObj.add("itineraryRecords", itineraryRecords);
		
		JsonArray leadershipRecords = new JsonArray();
		for ( int i = 0; i < ldrNotesIds.size(); i++ ) {
			JsonObject leadership = new JsonObject();
			leadership.addProperty("ldrLNID", ldrNotesIds.get(i));
			leadership.addProperty("ldrBU", ldrBUs.get(i));
			leadership.addProperty("ldrAttnd", ldrAttnds.get(i));
			leadership.addProperty("ldrLoc", ldrLocs.get(i));
			leadership.addProperty("ldrDate", ldrDates.get(i));
			leadershipRecords.add(leadership);
		}
		
		visitObj.add("leadershipRecords", leadershipRecords);
		
		
		Response response = database.save(visitObj);
		
		// TODO add some error handling mechanism here
		visitObj.addProperty("save_response", response.toString());
		visitObj.addProperty(Global.DOC_ID, response.getId());
		
		return visitObj.toString();
	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getVisits(@QueryParam(value="id") String id) {
		JsonObject retObj = new JsonObject();
		JsonArray retArray = new JsonArray();
		
		if ( id != null ) {
			
			JsonObject resultObj = database.find(JsonObject.class, id);
			retArray.add(resultObj);
			retObj.add("results", retArray);
			retObj.addProperty("status", "success");
			retObj.addProperty("count", "" + retArray.size());
		} else { 
			try {
				List<JsonObject> viewDocs = database.getViewRequestBuilder(
						Global.CLOUDANT_DD_ALL_VISITS, Global.CLOUDANT_VIEW_ALL_VISITS)
						.newRequest(Key.Type.STRING, Object.class)
						.includeDocs(true)
						.build()
						.getResponse()
						.getDocsAs(JsonObject.class);
				
				for (JsonObject doc : viewDocs) {
					
					/*JsonObject visitObj = database.find(JsonObject.class, docId);
					JsonElement visitObjType = visitObj.get(GlobalConsts.DOC_TYPE);
					if (visitObjType == null 
							|| GlobalConsts.VISIT_RECORD_TYPE.equals(
									visitObjType.getAsString())) {
						// only add if this is one of those visit records
						retArray.add(visitObj);
					}*/
					
					retArray.add(doc);
					
				}
				
				retObj.addProperty("status", "success");
				retObj.add("results", retArray);
				retObj.addProperty("count", "" + retArray.size());
				
			} catch (IOException e) {
				JsonObject errObj = new JsonObject();
				errObj.addProperty("status", "failed");
				errObj.addProperty("msg", 
						"Could not delete due to error: " + e.getMessage());
				
				logger.error("error occured while retrieving objects", e);
			}
		}
		
		return retObj.toString();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteVisits(@FormParam(value="id") String id) {
		JsonObject retObj = new JsonObject();
		
		try {
			retObj = database.find(JsonObject.class, id);
			
			if ( retObj != null ) {
				database.remove(retObj);
				retObj.addProperty("status", "success");
				retObj.addProperty("msg", "ID # " + id + "deleted successfully!");
			} else {
				retObj.addProperty("status", "failed");
				retObj.addProperty("msg", "Could not delete document: no results");
			}
			
		} catch ( Exception ex ) {
			retObj.addProperty("status", "error");
			retObj.addProperty("msg", "Could not delete due to error: " + ex.getMessage());
		}
		
		return retObj.toString();
	}
}
