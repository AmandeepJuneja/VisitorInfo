package com.ibm.vis.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.views.Key;
import com.google.gson.JsonObject;
import com.ibm.vis.resources.CloudVisit;
import com.ibm.vis.utils.CloudDBUtil;
import com.ibm.vis.utils.Global;

/**
 * Servlet implementation class DataPurger
 */
@WebServlet(description = "This servlet purges all old records from the database, that is all records that belong to the view past-visits.", urlPatterns = { "/purge" })
public class DataPurger extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String PURGE_PASS_REAL = System.getenv(Global.PURGE_PASS);
	protected Logger logger = Logger.getLogger(CloudVisit.class.getName());
	protected Database database;
       
    /**
     * @throws MalformedURLException 
     * @see HttpServlet#HttpServlet()
     */
    public DataPurger() throws MalformedURLException {
        super();
        database = CloudDBUtil.createInstance()
				.getDB(Global.CLOUDANT_DB_NAME, false);
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String purgePass = request.getParameter(Global.SERVLET_PARAM_PURGE_PASS);
				
		if ( purgePass == null || purgePass.trim().length() <= 0 ||
				!PURGE_PASS_REAL.equals(purgePass)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
					"Please provide a purge password to purge records!");
		}
		
		try {
			List<JsonObject> viewDocs = database.getViewRequestBuilder(
					Global.CLOUDANT_DD_PAST_VISITS, Global.CLOUDANT_VIEW_PAST_VISITS)
					.newRequest(Key.Type.STRING, Object.class)
					.includeDocs(true)
					.build()
					.getResponse()
					.getDocsAs(JsonObject.class);
			JSONArray resArr = new JSONArray();
			for ( JsonObject object : viewDocs ) {
				Response objRes = database.remove(object);
				JSONObject resObj = new JSONObject();
				resObj.append("id", objRes.getId());
				resObj.append("rev", objRes.getRev());
				resObj.append("statusCode", objRes.getStatusCode());
				resObj.append("reason", objRes.getReason());
				resObj.append("error", objRes.getError());
				resArr.put(resObj);
			}
			
			PrintWriter pw = response.getWriter();
			pw.println(resArr.toString());
			pw.flush();
		} catch ( Exception ex ) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					ex.getMessage());
		}
	}

}
