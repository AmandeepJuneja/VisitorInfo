package com.ibm.vis.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.cloudant.client.api.Database;
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
		// STEP 1 - Get all records
		List<String> allDocIds;
		
		// STEP 2 - Prepare & send the Excel file
		
	}

}
