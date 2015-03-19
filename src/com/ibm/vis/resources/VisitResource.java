package com.ibm.vis.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ibm.vis.utils.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

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
		
		DBCollection collection = DBUtil.createInstance()
		.getCollection(GlobalConsts.MDB_VISIT_REC_COLLECTION_NAME);		
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
}
