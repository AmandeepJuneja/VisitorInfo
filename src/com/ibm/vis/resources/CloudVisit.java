/*
 * VisitResource.java
 * (C) 2016 Siddhartha Ghosh / IBM India Pvt. Ltd.
 * All Rights Reserved.
 */
package com.ibm.vis.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.vis.utils.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * This is the older Visit Resource RESTful service handler class 
 * that relied on the MongoDB NoSQL database backend. Soon to be deprecated.
 * @author Siddhartha Ghosh (siddhartha.ghosh@in.ibm.com)
 *
 */
@Path("/cloudvisit")
public class CloudVisit {
	protected Logger logger = Logger.getLogger(CloudVisit.class.getName());
	protected Database database = CloudDBUtil.createInstance()
			.getDB(GlobalConsts.CLOUDANT_DB_NAME, false);
	
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
			@FormParam(value="ldrDate") List<String> ldrDates) {
		
		JsonObject visitObj = new JsonObject();
		
		visitObj.addProperty("visitTypeChoice", visitTypeChoice);
		visitObj.addProperty("industry", industry);
		visitObj.addProperty("accName", accName);
		visitObj.addProperty("palLFE", palLFE);
		visitObj.addProperty("cbc", cbc);
		visitObj.addProperty("hostMgr", hostMgr);
		visitObj.addProperty("visitAgenda", visitAgenda);
		
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
		
		return visitObj.toString();
	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getVisits(@QueryParam(value="id") String id) {
		JsonObject retObj = new JsonObject();
		
		if ( id != null ) {
			retObj = database.find(JsonObject.class, id);
			retObj.addProperty("status", "success");
		} else { 
			List<String> allDocIds;
			try {
				allDocIds = database.getAllDocsRequestBuilder().build().getResponse().getDocIds();
				JsonArray retArray = new JsonArray();
				for (String docId : allDocIds) {
					JsonObject visitObj = database.find(JsonObject.class, docId);
					retArray.add(visitObj);
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
