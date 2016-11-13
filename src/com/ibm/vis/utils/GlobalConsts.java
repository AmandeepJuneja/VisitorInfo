/*
 * GlobalConsts.java
 * (C) 2016 IBM India Pvt. Ltd.
 * All Rights Reserved.
 * 
 * This program is a part of the VisitorInformationManagement System.
 */
package com.ibm.vis.utils;

/**
 * This class contains all of the Global Constants for the Application.
 * @author <a href="mailto:siddhartha.ghosh@in.ibm.com">Siddhartha Ghosh</a>
 *
 */
public final class GlobalConsts {
	/* Mongo DB Settings */
	public static final String MDB_USER_ID = "ucm";
	public static final String MDB_PASSWORD = "iluvSAP2014$$";
	
	public static final String MDB_VISIT_REC_COLLECTION_NAME = "vis_visit";
	
	/* Cloudant DB Settings */
	public static final String CLOUDANT_DB_NAME = "vis";
	public static final String CLOUDANT_TEST_DB_NAME = "vis_testbed";
	
	/* CloudFoundry Environment Variable Names */
	public static final String ENV_SEED = "ID_GEN_SEED";
	public static final String ENV_PREFIX = "ID_GEN_PREFIX";
	public static final String ENV_SECTOR_INDUSTRY_MAP = "SECTOR_INDUSTRY_MAP";
	
	/* JSON Fields */
	public static final String DOC_TYPE = "type";
	public static final String DOC_ID = "_id";
	
	/* JSON Field Values */
	public static final String VISIT_RECORD_TYPE = "com.ibm.vis.visit_record";
}
