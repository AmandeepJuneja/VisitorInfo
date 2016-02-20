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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.cloudant.client.api.Database;
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
