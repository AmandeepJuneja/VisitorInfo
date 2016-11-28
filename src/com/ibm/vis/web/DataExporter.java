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
import org.json.JSONObject;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.Key;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.vis.resources.CloudVisit;
import com.ibm.vis.utils.CloudDBUtil;
import com.ibm.vis.utils.Global;

/**
 * Servlet implementation class DataExporter
 */
@WebServlet(description = "This servlet is used to export the data as an excel dump", urlPatterns = { "/xport" })
public class DataExporter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected Logger logger = Logger.getLogger(CloudVisit.class.getName());
	protected Database database;
	protected static final String[] HEADER_ROWS = {
			"VISIT ID",
			"VISIT TYPE",
			"SECTOR",
			"INDUSTRY",
			"ACCOUNT NAME",
			"PAL/LFE",
			"CBC",
			"HOSTING MANAGER", 
			"VISIT AGENDA",
			"VISITORS",
			"ITINERARY",
			"LEADERSHIP PARTICIPATION",
			"EXECUTIVE OWNER(TCV > 10 Million)",
			"OPPORTUNITY TCV(in $M)",
			"DELIVERY TYPE",
			"CREATED BY",
			"LAST MODIFIED BY"
	};
       
    /**
     * @throws MalformedURLException 
     * @see HttpServlet#HttpServlet()
     */
    public DataExporter() throws MalformedURLException {
        super();
        // TODO Auto-generated constructor stub
        database = CloudDBUtil.createInstance()
				.getDB(Global.CLOUDANT_DB_NAME, false);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// STEP 1 - Get all records & Prepare the excel file
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("VIS DUMP");
		
		String mode = request.getParameter(Global.SERVLET_PARAM_EXPORTER_MODE);
		String designDoc = (mode != null && Global.SERVLET_PARAM_EXPORTER_MODE_VALUE_ARCHIVE.equals(mode))?
				Global.CLOUDANT_DD_PAST_VISITS:Global.CLOUDANT_DD_ALL_VISITS;
		String view = (mode != null && Global.SERVLET_PARAM_EXPORTER_MODE_VALUE_ARCHIVE.equals(mode))?
				Global.CLOUDANT_VIEW_PAST_VISITS:Global.CLOUDANT_VIEW_ALL_VISITS;
		
		List<JsonObject> allDocuments = database.getViewRequestBuilder(designDoc, view).newRequest(Key.Type.STRING, Object.class)
				.includeDocs(true)
				.build()
				.getResponse()
				.getDocsAs(JsonObject.class);
		int rowNum = 0;
		
		Row headerRow = sheet.createRow(rowNum++);
		int colNum = 0;
		for ( String header : HEADER_ROWS ) {
			headerRow.createCell(colNum++).setCellValue(header);
		}
		
		for (JsonObject visitObj : allDocuments) {
			colNum = 0;
			Row dataRow = sheet.createRow(rowNum++);
			if ( visitObj.get("_id") != null ) {
				String id = visitObj.get("_id").getAsString();
				dataRow.createCell(colNum++).setCellValue(id);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("visitTypeChoice") != null ) {
				String visitTypeChoice = visitObj.get("visitTypeChoice").getAsString();
				if ( Global.VISIT_TYPE_CHOICE_IBM.equals(visitTypeChoice) ) {
					dataRow.createCell(colNum++).setCellValue(Global.VISIT_TYPE_CHOICE_IBM_VALUE);
				} else if ( Global.VISIT_TYPE_CHOICE_CLIENT.equals(visitTypeChoice) ) {
					dataRow.createCell(colNum++).setCellValue(Global.VISIT_TYPE_CHOICE_CLIENT_VALUE);
				} else if ( Global.VISIT_TYPE_CHOICE_BOTH.equals(visitTypeChoice) ) {
					dataRow.createCell(colNum++).setCellValue(Global.VISIT_TYPE_CHOICE_BOTH_VALUE);
				} else {
					dataRow.createCell(colNum++).setCellValue("");
				}
			}
			
			if ( visitObj.get("industry") != null ) {
				String industry = visitObj.get("industry").getAsString();
				dataRow.createCell(colNum++).setCellValue(industry);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("sector") != null ) {
				String sector = visitObj.get("sector").getAsString();
				dataRow.createCell(colNum++).setCellValue(sector);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("accName") != null ) {
				String accName = visitObj.get("accName").getAsString();
				dataRow.createCell(colNum++).setCellValue(accName);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("palLFE") != null ) {
				String palLFE = visitObj.get("palLFE").getAsString();
				dataRow.createCell(colNum++).setCellValue(palLFE);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("cbc") != null ) {
				String cbc = visitObj.get("cbc").getAsString();
				dataRow.createCell(colNum++).setCellValue(cbc);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("hostMgr") != null ) {
				String hostMgr = visitObj.get("hostMgr").getAsString();
				dataRow.createCell(colNum++).setCellValue(hostMgr);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("visitAgenda") != null ) {
				String visitAgenda = visitObj.get("visitAgenda").getAsString();
				dataRow.createCell(colNum++).setCellValue(visitAgenda);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			// NESTED FIELD -- VISITORS
			if ( visitObj.get("visitorRecords") != null ) {
				JsonArray visitorRecords = visitObj.get("visitorRecords").getAsJsonArray();
				Cell cell = dataRow.createCell(colNum++);
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
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			// NESTED FIELD -- ITINERARY
			if ( visitObj.get("itineraryRecords") != null ) {
				JsonArray itineraryRecords = visitObj.get("itineraryRecords").getAsJsonArray();
				Cell cell = dataRow.createCell(colNum++);
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
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			// NESTED FIELD -- LEADERSHIP PARTICIPATION
			if ( visitObj.get("leadershipRecords") != null ) {
				JsonArray leadershipRecords = visitObj.get("leadershipRecords").getAsJsonArray();
				Cell cell = dataRow.createCell(colNum++);
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
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("execOwnerTCV") != null ) {
				String execOwnerTCV = visitObj.get("execOwnerTCV").getAsString();
				dataRow.createCell(colNum++).setCellValue(execOwnerTCV);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("opportunityTCV") != null ) {
				String opportunityTCV = visitObj.get("opportunityTCV").getAsString();
				dataRow.createCell(colNum++).setCellValue(opportunityTCV);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("deliveryTypeChoice") != null ) {
				String deliveryTypeChoice = visitObj.get("deliveryTypeChoice").getAsString();
				if (deliveryTypeChoice != null) {
					if ( deliveryTypeChoice.equals("E") ) {
						dataRow.createCell(colNum++).setCellValue("Existing Delivery");
					} else if ( deliveryTypeChoice.equals("N") ) {
						dataRow.createCell(colNum++).setCellValue("New Opportunity");
					} else {
						dataRow.createCell(colNum++).setCellValue("Unknown");
					}
				} else {
					dataRow.createCell(colNum++).setCellValue("Null");
				}
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("createdBy") != null ) {
				String createdBy = visitObj.get("createdBy").getAsString();
				dataRow.createCell(colNum++).setCellValue(createdBy);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
			}
			
			if ( visitObj.get("lastUpdatedBy") != null ) {
				String lastUpdatedBy = visitObj.get("lastUpdatedBy").getAsString();
				dataRow.createCell(colNum++).setCellValue(lastUpdatedBy);
			} else {
				dataRow.createCell(colNum++).setCellValue("");
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
