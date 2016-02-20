package com.ibm.vis.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.vis.resources.CloudVisit;
import com.ibm.vis.utils.CloudDBUtil;
import com.ibm.vis.utils.GlobalConsts;

/**
 * Servlet implementation class DataExporter
 */
@WebServlet(description = "This servlet is used to export the data as an excel dump", urlPatterns = { "/xport" })
public class DataExporter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected Logger logger = Logger.getLogger(CloudVisit.class.getName());
	protected Database database;
       
    /**
     * @throws MalformedURLException 
     * @see HttpServlet#HttpServlet()
     */
    public DataExporter() throws MalformedURLException {
        super();
        // TODO Auto-generated constructor stub
        database = CloudDBUtil.createInstance()
				.getDB(GlobalConsts.CLOUDANT_DB_NAME, false);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// STEP 1 - Get all records & Prepare the excel file
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("VIS DUMP");
		
		List<String> allDocIds = database.getAllDocsRequestBuilder().build().getResponse().getDocIds();
		int rowNum = 0;
		Row headerRow = sheet.createRow(rowNum++);
		headerRow.createCell(0).setCellValue("VISIT ID");
		headerRow.createCell(1).setCellValue("INDUSTRY");
		headerRow.createCell(2).setCellValue("ACCOUNT NAME");
		headerRow.createCell(3).setCellValue("PAL/LFE");
		headerRow.createCell(4).setCellValue("CBC");
		headerRow.createCell(5).setCellValue("HOSTING MANAGER");
		headerRow.createCell(6).setCellValue("VISIT AGENDA");
		headerRow.createCell(7).setCellValue("VISITORS");
		headerRow.createCell(8).setCellValue("ITINERARY");
		headerRow.createCell(9).setCellValue("LEADERSHIP PARTICIPATION");
		headerRow.createCell(10).setCellValue("EXECUTIVE OWNER(TCV > 10 Million)");
		headerRow.createCell(11).setCellValue("OPPORTUNITY TCV(in $M)");
		headerRow.createCell(12).setCellValue("DELIVERY TYPE");
		
		for (String docId : allDocIds) {
			JsonObject visitObj = database.find(JsonObject.class, docId);
			
			Row dataRow = sheet.createRow(rowNum++);
			if ( visitObj.get("_id") != null ) {
				String id = visitObj.get("_id").getAsString();
				dataRow.createCell(0).setCellValue(id);
			}
			
			if ( visitObj.get("industry") != null ) {
				String industry = visitObj.get("industry").getAsString();
				dataRow.createCell(1).setCellValue(industry);
			}
			
			if ( visitObj.get("accName") != null ) {
				String accName = visitObj.get("accName").getAsString();
				dataRow.createCell(2).setCellValue(accName);
			}
			
			if ( visitObj.get("palLFE") != null ) {
				String palLFE = visitObj.get("palLFE").getAsString();
				dataRow.createCell(3).setCellValue(palLFE);
			}
			
			if ( visitObj.get("cbc") != null ) {
				String cbc = visitObj.get("cbc").getAsString();
				dataRow.createCell(4).setCellValue(cbc);
			}
			
			if ( visitObj.get("hostMgr") != null ) {
				String hostMgr = visitObj.get("hostMgr").getAsString();
				dataRow.createCell(5).setCellValue(hostMgr);
			}
			
			if ( visitObj.get("visitAgenda") != null ) {
				String visitAgenda = visitObj.get("visitAgenda").getAsString();
				dataRow.createCell(6).setCellValue(visitAgenda);
			}
			
			// NESTED FIELD -- VISITORS
			if ( visitObj.get("visitorRecords") != null ) {
				JsonArray visitorRecords = visitObj.get("visitorRecords").getAsJsonArray();
				Cell cell = dataRow.createCell(7);
//				CellStyle style = workbook.createCellStyle();
//			    style.setWrapText(true);
//			    cell.setCellStyle(style);
			    StringBuffer visitorsBuffer = new StringBuffer();
				for ( JsonElement visitorRecordElement : visitorRecords ) {
					String visitorName = visitorRecordElement.getAsJsonObject().get("visitorName").getAsString();
					String visitorRole = visitorRecordElement.getAsJsonObject().get("visitorRole").getAsString();
					String visitorPrimary = visitorRecordElement.getAsJsonObject().get("visitorPrimary").getAsString();
					visitorsBuffer.append(visitorName + " ");
					visitorsBuffer.append("[Role: " + visitorRole + "]");
					if ( visitorPrimary != null && visitorPrimary.trim().length() > 0 
							&& (visitorPrimary.equalsIgnoreCase("YES") || visitorPrimary.equalsIgnoreCase("Y") ) ) {
						visitorsBuffer.append(" (Primary)");
					}
					visitorsBuffer.append("\n");
				}
				cell.setCellValue(visitorsBuffer.toString());
			}
			
			// NESTED FIELD -- ITINERARY
			if ( visitObj.get("itineraryRecords") != null ) {
				JsonArray itineraryRecords = visitObj.get("itineraryRecords").getAsJsonArray();
				Cell cell = dataRow.createCell(8);
//				CellStyle style = workbook.createCellStyle();
//			    style.setWrapText(true);
//			    cell.setCellStyle(style);
			    StringBuffer itineraryBuffer = new StringBuffer();
			    for ( JsonElement itineraryElement : itineraryRecords ) {
			    	itineraryBuffer.append(itineraryElement.getAsJsonObject().get("itiLoc").getAsString());
			    	itineraryBuffer.append(" (From: " + itineraryElement.getAsJsonObject().get("itiStart").getAsString());
			    	itineraryBuffer.append(" To: " + itineraryElement.getAsJsonObject().get("itiEnd").getAsString() + ")");
			    	itineraryBuffer.append("\n");
			    }
			    cell.setCellValue(itineraryBuffer.toString());
			}
			
			// NESTED FIELD -- LEADERSHIP PARTICIPATION
			if ( visitObj.get("leadershipRecords") != null ) {
				JsonArray leadershipRecords = visitObj.get("leadershipRecords").getAsJsonArray();
				Cell cell = dataRow.createCell(9);
//				CellStyle style = workbook.createCellStyle();
//			    style.setWrapText(true);
//			    cell.setCellStyle(style);
			    StringBuffer leadershipBuffer = new StringBuffer();
			    for ( JsonElement leadershipElement : leadershipRecords ) {
			    	leadershipBuffer.append(leadershipElement.getAsJsonObject().get("ldrLNID").getAsString());
			    	leadershipBuffer.append(" (" + leadershipElement.getAsJsonObject().get("ldrBU").getAsString() + ")");
			    	leadershipBuffer.append(" (" + leadershipElement.getAsJsonObject().get("ldrAttnd").getAsString() + ")");
			    	leadershipBuffer.append(" (" + leadershipElement.getAsJsonObject().get("ldrLoc").getAsString() + ")");
			    	leadershipBuffer.append(" (" + leadershipElement.getAsJsonObject().get("ldrDate").getAsString() + ")");
			    	leadershipBuffer.append("\n");
			    }
			    cell.setCellValue(leadershipBuffer.toString());
			}
			
			if ( visitObj.get("execOwnerTCV") != null ) {
				String execOwnerTCV = visitObj.get("execOwnerTCV").getAsString();
				dataRow.createCell(10).setCellValue(execOwnerTCV);
			}
			
			if ( visitObj.get("opportunityTCV") != null ) {
				String opportunityTCV = visitObj.get("opportunityTCV").getAsString();
				dataRow.createCell(11).setCellValue(opportunityTCV);
			}
			
			if ( visitObj.get("deliveryTypeChoice") != null ) {
				String deliveryTypeChoice = visitObj.get("deliveryTypeChoice").getAsString();
				if (deliveryTypeChoice != null) {
					if ( deliveryTypeChoice.equals("E") ) {
						dataRow.createCell(12).setCellValue("Existing Delivery");
					} else if ( deliveryTypeChoice.equals("N") ) {
						dataRow.createCell(12).setCellValue("New Opportunity");
					} else {
						dataRow.createCell(12).setCellValue("Unknown");
					}
				} else {
					dataRow.createCell(12).setCellValue("Null");
				}
				
			}
		}
		
		// STEP 2 - Send the Excel file
		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=vis-dump.xls");
		OutputStream outStream = response.getOutputStream();
		workbook.write(outStream); // Write workbook to response.
		outStream.flush();
		outStream.close();		
	}

}
