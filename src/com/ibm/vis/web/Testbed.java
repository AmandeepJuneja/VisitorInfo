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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.vis.utils.IdGenerator;

/**
 * The purpose of this servlet is to provide a safe platform for testing without touching the production data.
 * @author Siddhartha Ghosh
 * Servlet implementation class Testbed
 */
@WebServlet(description = "A testing servlet meant to test out certain scenarios.", urlPatterns = { "/test.do" })
public class Testbed extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Testbed() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter pw = response.getWriter();
		for ( int i = 0; i < 10; i++ ) {
			pw.println(IdGenerator.nextVisitId());
		}
	}

}
