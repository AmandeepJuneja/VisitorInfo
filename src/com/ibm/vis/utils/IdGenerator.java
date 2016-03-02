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
	
	private static String seed;
	private static int counter;
	
	static { 
		seed = System.getenv(GlobalConsts.ENV_SEED);
		counter = Integer.parseInt(seed);
	}

	
	/**
	 * Get the next visit id in sequence.
	 * @return the next visit id in sequence in the format VIS_DDYY-CC
	 */
	public static String nextVisitId() {
		String seed = System.getenv(GlobalConsts.ENV_SEED);
		String prefix = System.getenv(GlobalConsts.ENV_PREFIX);
		
		Calendar cal = Calendar.getInstance();
		String dayOfYear = "" + cal.get(Calendar.DAY_OF_YEAR);
		String year = ("" + cal.get(Calendar.YEAR)).substring(2, 4);
		
		
		return prefix + dayOfYear + year + "-" + counter++;
	}
	
}
