/*
 * IdGenerator.java
 * 
 * (C) 2016 IBM India Pvt. Ltd.
 * All Rights Reserved.
 * 
 * This program is a part of the VisitorInformationManagement System.
 * 
 */
package com.ibm.vis.utils;

import java.util.Calendar;

/**
 * This class is used to generate legible Visitor Record ids.
 * @author Siddhartha Ghosh
 *
 */
public class IdGenerator {
	
	public static final String SEED_ENV_NAME = "ID_GEN_SEED";
	public static final String PREFIX_ENV_NAME = "ID_GEN_PREFIX";
	
	private static String seed;
	private static int counter;
	
	static { 
		seed = System.getenv(SEED_ENV_NAME);
		counter = Integer.parseInt(seed);
	}

	
	/**
	 * Get the next visit id in sequence.
	 * @return the next visit id in sequence in the format VIS_DDYY-CC
	 */
	public static String nextVisitId() {
		String seed = System.getenv(SEED_ENV_NAME);
		String prefix = System.getenv(PREFIX_ENV_NAME);
		
		Calendar cal = Calendar.getInstance();
		String dayOfYear = "" + cal.get(Calendar.DAY_OF_YEAR);
		String year = ("" + cal.get(Calendar.YEAR)).substring(2, 4);
		
		
		return prefix + dayOfYear + year + "-" + counter++;
	}
	
}
