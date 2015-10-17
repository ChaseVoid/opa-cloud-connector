package com.ajay.opa.cloud.utils;

import com.oracle.xmlns.policyautomation.hub._12_0.metadata.types.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.TimeZone;


/**
 * This class is responsible for handling Load requests to the service. The 'camera' table 
 * is the only table which is loaded from, so a query for all fields of 'camera' is made, 
 * and the response is filled for each field requested.
 *  @version 2014.05.08
 */
public abstract class LoadTool {
	
	private final static Logger LOGGER = LogManager.getLogger(LoadTool.class);
	private final static CameraDAOImpl cameraDAO = new CameraDAOImpl();
	
	/**
	 * This method processes a LoadRequest by requesting a CameraDAOImpl to load values 
	 * for each of the fields in the request and using the values from a List of returned Camera 
	 * objects to fill the LoadResponse.
	 * @param request The request sent by web determinations.
	 * @return The LoadResponse 
	 * @throws RequestFault_Exception 
	 */
	public static LoadResponse processRequest(LoadRequest request) throws RequestFault_Exception {
		
		LoadResponse response = new LoadResponse();
		response.setTables(new LoadData());
		
		List<DataTable> responseTables = response.getTables().getTable();
		
		// generate matching response table for each request table
		for (Table requestTable : request.getTables().getTable()) {
			DataTable table = new DataTable();
			table.setName(requestTable.getName());
			responseTables.add(table);
		}
		
		/*// get all cameras
		List<Camera> cameras = cameraDAO.getCameras();
		
		// put cameras into the response
		LoadTool.populateCameraTable(request, response, cameras);
		LoadTool.insertCameraLinksIntoProductTable(response, cameras);*/
		
		return response;
	}
	
	/**
	 * This method takes an instantiated List of Cameras and populates the 'camera' 
	 * table of the LoadResponse. The fields generated are based on those given in 
	 * the LoadRequest.
	 * @param request LoadRequest sent from Web Determinations.
	 * @param response Initialized LoadResponse to further process.
	 * @param cameras Initialized List of Camera objects.
	 * @throws RequestFault_Exception
	 */
	/*private static void populateCameraTable(LoadRequest request, LoadResponse response,
			List<Camera> cameras) throws RequestFault_Exception {
		
		DataTable cameraTable = null;
		Table requestTable = null;
		
		for (DataTable table : response.getTables().getTable()) {
			if (table.getName().equals(Camera.OPM_TABLE_NAME)) {
				cameraTable = table;
				break;
			}
		}
		
		for (Table table : request.getTables().getTable()) {
			if (table.getName().equals(Camera.OPM_TABLE_NAME)) {
				requestTable = table;
				break;
			}
		}
		
		TimeZone requestTimezone = TimeZone.getTimeZone(request.getTimezone());
		
		if (cameraTable == null || requestTable == null)
			return;
		
		List<DataRow> cameraTableRows = cameraTable.getRow();
		
		for (Camera camera : cameras) {
			DataRow newCameraRow = new DataRow();
			cameraTableRows.add(newCameraRow);
			
			newCameraRow.setId(camera.getModel());
			
			List<DataField> fieldList = newCameraRow.getField();
			
			for (Field requestField : requestTable.getField()) {
				DataField field = new DataField();
				fieldList.add(field);
				
				field.setName(requestField.getName());

				Object value = null;
				switch (field.getName()) {
				case Camera.COL_MODEL:
					value = camera.getModel();
					break;
				case Camera.COL_COLOR_CODE:
					value = camera.getColorCode();
					break;
				case Camera.COL_PRICE:
					value = camera.getPrice();
					break;
				case Camera.COL_HAS_PORTRAIT:
					value = camera.isHasPortrait();
					break;
				case Camera.COL_HAS_TIMER:
					value = camera.isHasTimer();
					break;
				case Camera.COL_HAS_REDEYE:
					value = camera.isHasRedeye();
					break;
				case Camera.COL_HAS_FLASH:
					value = camera.isHasFlash();
					break;
				default:
					ExceptionTool.throwRequestFaultEx("Web service error.", 
							String.format("Column '%s' not found.", field.getName()), LOGGER);
				}
				
				setFieldValue(field, value, requestTimezone);
			}
			
			// Each instance of a DataRow within the 'camera' table does NOT contain any Links to 
			// the 'Products' DataRow of the 'product' table, because the metadata only describes a 
			// one to many relationship in a product table to camera, not the other way around.
			// So for this table, createLinkRefList(dataRow, targetTable) is not called.
		}
	}*/

	/**
	 * This method sets the correct field value based on the data type passed to it.
	 * @param field The field to set the value of.
	 * @param value An object representing the field value.
	 * @throws RequestFault_Exception When the object is not an expected type.
	 */
	private static void setFieldValue(DataField field, Object value, TimeZone requestTimezone) throws RequestFault_Exception {

		if (value == null)
			field.setUnknownVal(new UnknownValue());
		else if (value instanceof Boolean)
			field.setBooleanVal((Boolean)value);
		else if (value instanceof BigDecimal)
			field.setNumberVal((BigDecimal)value);
		else if (value instanceof String)
			field.setTextVal(value.toString());
		else if (value instanceof Time)
			field.setTimeVal(CalendarTool.toXMLGregorianCalendar((Time)value));
		else if (value instanceof Timestamp)
			field.setDatetimeVal(CalendarTool.toXMLGregorianCalendar((Timestamp)value, requestTimezone));
		else if (value instanceof Date)
			field.setDateVal(CalendarTool.toXMLGregorianCalendar((Date)value));
		else {
			ExceptionTool.throwRequestFaultEx("Unexpected object type.", String.format(
					"An unexpected object type '%s' was passed into LoadTool.setFieldValue().\n"
					+ "Expected types include: String, Boolean, java.math.BigDecimal, "
					+ "java.sql.Date, java.sql.Time, java.sql.Timestamp;", value.getClass()), LOGGER);
		}
			
		// note that only the field types the service expects are dealt with as possible values
	}

	/**
	 * This method creates a link for each Camera in the instance of a 'product' table, 'Products'.
	 * @param response The initialized LoadResponse.
	 * @param cameraList Initialized List of Camera objects.
	 */
	/*private static void insertCameraLinksIntoProductTable(LoadResponse response, List<Camera> cameraList) {
		List<DataTable> tables = response.getTables().getTable();
		DataTable productTable = null;
		for (DataTable table : tables) {
			if (table.getName().equals(MetadataTool.PRODUCTS_PSEUDO_TABLE_NAME)) {
				productTable = table;
				break;
			}
		}

		if (productTable == null)
			return;
		
		// create new 'Products' row
		DataRow productsRow = new DataRow();
		productsRow.setId(MetadataTool.PRODUCTS_PSEUDO_TABLE_NAME);

		// add new row to table
		productTable.getRow().add(productsRow);
		
		List<LinkRef> productLinkRefs = createLinkRefList(productsRow, Camera.LINKED_TO_NAME, Camera.OPM_TABLE_NAME);
		
		for (Camera camera : cameraList){
			LinkRef productLinkRef = new LinkRef();
			productLinkRef.setId(camera.getModel());
			productLinkRefs.add(productLinkRef);
		}
	}
	*/
	/**
	 * This method initializes and returns a LinkRef List in the given DataRow, targeted at a 
	 * given Table name.
	 * @param dataRow The DataRow to create the List in.
	 * @param linkTargetTable The name of the table each of the LinkRefs will target.
	 * @return The List of LinkRefs
	 */
	private static List<LinkRef> createLinkRefList(DataRow dataRow, String linkName, String linkTargetTable) {
		// create new Link
		DataLink productLink = new DataLink();
		productLink.setName(linkName);
		productLink.setTarget(linkTargetTable);
		
		// add new link to row
		dataRow.getLink().add(productLink);
		
		// get reference list of new link
		return productLink.getRef();
	}
}
