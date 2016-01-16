/*
 * VisitResource.java
 * (C) 2016 Siddhartha Ghosh / IBM India Pvt. Ltd.
 * All Rights Reserved.
 */
package com.ibm.vis.resources;

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

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

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
@Path("/visit")
public class VisitResource {
	DBCollection collection = DBUtil.createInstance()
			.getCollection(GlobalConsts.MDB_VISIT_REC_COLLECTION_NAME);	
	
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
		
		System.out.println("GOT A REQUEST! *~*~*~*~*~*~*~ ");	
		BasicDBObject visitObj = new BasicDBObject();
		
		visitObj.append("visitTypeChoice", visitTypeChoice);
		visitObj.append("industry", industry);
		visitObj.append("accName", accName);
		visitObj.append("palLFE", palLFE);
		visitObj.append("cbc", cbc);
		visitObj.append("hostMgr", hostMgr);
		visitObj.append("visitAgenda", visitAgenda);
		
		ArrayList<BasicDBObject> visitorRecords = new ArrayList<BasicDBObject>();
		for ( int i = 0; i < visitorNames.size(); i++ ) {
			BasicDBObject visitor = new BasicDBObject();
			visitor.append("visitorName", visitorNames.get(i));
			visitor.append("visitorRole", visitorRoles.get(i));
			visitor.append("visitorPrimary", visitorPrimaries.get(i));
			
			visitorRecords.add(visitor);
		}
		
		visitObj.append("visitorRecords", visitorRecords);
		
		ArrayList<BasicDBObject> itineraryRecords = new ArrayList<BasicDBObject>();
		for ( int i = 0; i < itiLocations.size(); i++ ) {
			BasicDBObject itinerary = new BasicDBObject();
			itinerary.append("itiLoc", itiLocations.get(i));
			itinerary.append("itiStart", itiStartDates.get(i));
			itinerary.append("itiEnd", itiEndDates.get(i));
			itineraryRecords.add(itinerary);
		}
		
		visitObj.append("itineraryRecords", itineraryRecords);
		
		ArrayList<BasicDBObject> leadershipRecords = new ArrayList<BasicDBObject>();
		for ( int i = 0; i < ldrNotesIds.size(); i++ ) {
			BasicDBObject leadership = new BasicDBObject();
			leadership.append("ldrLNID", ldrNotesIds.get(i));
			leadership.append("ldrBU", ldrBUs.get(i));
			leadership.append("ldrAttnd", ldrAttnds.get(i));
			leadership.append("ldrLoc", ldrLocs.get(i));
			leadership.append("ldrDate", ldrDates.get(i));
			leadershipRecords.add(leadership);
		}
		
		visitObj.append("leadershipRecords", leadershipRecords);
		
		
		collection.insert(visitObj);
		
		return visitObj.toString();
	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getVisits(@QueryParam(value="id") String id) {
		System.out.println("GOT A REQUEST! *~*~*~*~*~*~*~ ");
		
		
		BasicDBObject retObj = new BasicDBObject();
		DBCursor cursor = null;
		
		if ( id != null ) {
//			
			ObjectId objId = new ObjectId(id);
			System.out.println("Received a request for deletion with id = " + id);
			cursor = collection.find(new BasicDBObject("_id", objId));
		} else { 
			cursor = collection.find();
		}
		
		
		
		try {
			if ( cursor.count() > 0 ) {
				retObj.append("status", "success");
				retObj.append("results", cursor.toArray());
				retObj.append("count", cursor.count());
			} else {
				retObj.append("status", "failed");
				retObj.append("msg", "No results found!");
			}
		} catch (Exception ex) {
			retObj.append("status", "error");
			retObj.append("msg", "An exception occured: " + ex.getMessage());
		} finally {
			cursor.close();
		}
		
		return retObj.toString();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteVisits(@FormParam(value="id") String id) {
		BasicDBObject retObj = new BasicDBObject();
		
		ObjectId objId = new ObjectId(id);
		System.out.println("Received a request for deletion with id = " + id);
		
		try {
			DBObject toBeDel =  collection.findOne(new BasicDBObject("_id", objId));
			System.out.println("About to delete object: " + toBeDel);
			if ( toBeDel != null ) {
				collection.remove(toBeDel);
				retObj.append("status", "success");
				retObj.append("msg", "ID # " + id + "deleted successfully!");
			} else {
				retObj.append("status", "failed");
				retObj.append("msg", "Could not delete document: no results");
			}
			
		} catch ( Exception ex ) {
			retObj.append("status", "error");
			retObj.append("msg", "Could not delete due to error: " + ex.getMessage());
		}
		
		return retObj.toString();
	}
}
