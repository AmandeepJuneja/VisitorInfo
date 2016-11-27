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
	public static final String PURGE_PASS = "PURGE_PASS"; // password for purging old records.
	
	/* JSON Fields */
	public static final String DOC_TYPE = "type";
	public static final String DOC_ID = "_id";
	
	/* JSON Field Values */
	public static final String VISIT_RECORD_TYPE = "com.ibm.vis.visit_record";
	
	/* Visit Type Choices */
	public static final String VISIT_TYPE_CHOICE_CLIENT = "C";
	public static final String VISIT_TYPE_CHOICE_CLIENT_VALUE = "Client Only";
	public static final String VISIT_TYPE_CHOICE_IBM = "I";
	public static final String VISIT_TYPE_CHOICE_IBM_VALUE = "IBM Only";
	public static final String VISIT_TYPE_CHOICE_BOTH = "B";
	public static final String VISIT_TYPE_CHOICE_BOTH_VALUE = "Both Client & IBM";
	
	/* Cloudant DB Design Docs */
	public static final String CLOUDANT_DD_ALL_VISITS = "allVisitsDD";
	public static final String CLOUDANT_DD_PAST_VISITS = "pastVisitsDD";
	
	/* Cloudant DB Views */
	public static final String CLOUDANT_VIEW_ALL_VISITS = "all-visits";
	public static final String CLOUDANT_VIEW_PAST_VISITS = "past-visits";
	public static final String CLOUDANT_VIEW_FUTURE_VISITS = "future-visits";
	
	/* Servlet Parameters */
	public static final String SERVLET_PARAM_EXPORTER_MODE = "mode";
	public static final String SERVLET_PARAM_EXPORTER_MODE_VALUE_ARCHIVE = "archive";
}
