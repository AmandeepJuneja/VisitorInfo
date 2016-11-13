/*
 * Testbed.java
 * 
 * (C) 2016 IBM India Pvt. Ltd.
 * All Rights Reserved.
 * 
 * This program is a part of the VisitorInformationManagement System.
 */
package com.ibm.vis.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Document;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.vis.utils.CloudDBUtil;
import com.ibm.vis.utils.GlobalConsts;
import com.ibm.vis.utils.IdGenerator;

/**
 * The purpose of this servlet is to provide a safe platform for testing without touching the production data.
 * @author Siddhartha Ghosh
 * Servlet implementation class Testbed
 */
@WebServlet(description = "A testing servlet meant to test out certain scenarios.", urlPatterns = { "/test.do" })
public class Testbed extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Database database;
	private JsonParser parser;
	
       
    /**
     * @throws MalformedURLException 
     * @see HttpServlet#HttpServlet()
     */
    public Testbed() throws MalformedURLException {
        super();
        database = CloudDBUtil.createInstance()
				.getDB(GlobalConsts.CLOUDANT_DB_NAME, false);
        parser = new JsonParser();
    }

	/**
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter pw = response.getWriter();
		
		pw.println("Welcome User: " + request.getRemoteUser());		
		pw.println("Your Principal: " + request.getUserPrincipal());
		
		pw.flush();
	}

}
