package com.ajay.opa.cloud.utils;

import com.ajay.opa.cloud.metadata.Field;
import com.ajay.opa.cloud.metadata.*;
import com.ajay.opa.cloud.metadata.Link;
import com.ajay.opa.cloud.metadata.Table;
import com.oracle.xmlns.policyautomation.hub._12_0.metadata.types.*;

import java.util.List;


/**
 * This class builds an abstracted set of metadata based upon a subset of the Camera Company 
 * database, exposing only 3 of the tables to OPA. The class exposes a single method to build 
 * the WSDL defined GetMetadataResponse from this abstracted layer.
 * @version 2014.05.06
 */
public final class MetadataTool {
	
	//public final static List<Table> ABSTRACT_META_TABLES = buildMetadata();

	// This 'Products' table does NOT represent a table in the schema, instead it is used to allow 
	// OPM to map to a single Global table which then link to a table with multiple rows, allowing 
	// the input of multiple entities.
	public final static String PRODUCTS_PSEUDO_TABLE_NAME = "Products";
	
	/**
	 * Builds the WSDL defined response from an abstracted metadata layer.
	 * @return The completed WSDL defined GetMetadataResponse.
	 */
	public static GetMetadataResponse buildMetadataResponse() {
		
		GetMetadataResponse response = new GetMetadataResponse();
		List<MetaTable> responseTableList = response.getTable();
		
		// Create a response Table for each Table defined in metaTables
		/*for (Table abstractTable : ABSTRACT_META_TABLES) {
			
			MetaTable responseTable = new MetaTable();
			buildResponseTable(abstractTable, responseTable);
			responseTableList.add(responseTable);
		}*/
		
		return response;
	}

	/**
	 * Builds a response Table from an abstract Table.
	 * @param abstractTable The abstract Table.
	 * @param responseTable The response Table.
	 */
	private static void buildResponseTable(Table abstractTable,
			MetaTable responseTable) {

		responseTable.setName(abstractTable.getName());
		responseTable.setCanBeInput(abstractTable.isCanBeInput());
		responseTable.setCanBeOutput(abstractTable.isCanBeOutput());
		
		buildResponseTableFields(abstractTable, responseTable);
		buildResponseTableLinks(abstractTable, responseTable);
	}

	/**
	 * Builds a Response Table's fields from an abstract Table's fields.
	 * @param abstractTable The abstract Table.
	 * @param responseTable The response Table.
	 */
	private static void buildResponseTableFields(Table abstractTable,
			MetaTable responseTable) {

		List<MetaField> responseFields = responseTable.getField();
		
		// Fill the field list for the response table with values from the abstract metadata table
		for (Field abstractField : abstractTable.getFields()) {
			MetaField responseField = new MetaField();
			responseField.setName(abstractField.getName());
			responseField.setType(convertFieldType(abstractField.getType()));
			responseField.setCanBeInput(abstractField.isCanBeInput());
			responseField.setCanBeOutput(abstractField.isCanBeOutput());
			responseField.setIsRequired(abstractField.isRequired());
			
			responseFields.add(responseField);
		}
	}
	
	/**
	 * Builds a Response Table's links from an abstract Table's links.
	 * @param abstractTable The abstract Table.
	 * @param responseTable The response Table.
	 */
	private static void buildResponseTableLinks(Table abstractTable,
			MetaTable responseTable) {
		
		List<MetaLink> links = responseTable.getLink();
		
		// Fill the link list for the response table with values from the abstract metadata table
		for (Link abstractMetaLink : abstractTable.getLinks()) {
			MetaLink responseLink = new MetaLink();
			responseLink.setName(abstractMetaLink.getName());
			responseLink.setTarget(abstractMetaLink.getTarget());
			responseLink.setCardinality(convertCardinality(abstractMetaLink.getCardinality()));
			
			links.add(responseLink);
		}
	}

	/**
	 * Converts from the abstract FieldType to the FieldDataType as defined by WSDL.
	 * @param abstractFieldType The abstract enumeration.
	 * @return The WSDL defined type.
	 */
	private static MetaFieldDataType convertFieldType(FieldType abstractFieldType) {
		switch (abstractFieldType) {
		case STRING:
			return MetaFieldDataType.STRING;
		case DATETIME:
			return MetaFieldDataType.DATETIME;
		case BOOLEAN:
			return MetaFieldDataType.BOOLEAN;
		case DATE:
			return MetaFieldDataType.DATE;
			
		// the 'Number' OPM type may output a decimal value, so the service must handle dealing 
		// with conversion from decimal to integer or long, and the policy modeler is required 
		// to set restrictions on an attribute if it is expected to only output integer values.
		case INTEGER:
		case LONG:
		case DECIMAL:
			return MetaFieldDataType.DECIMAL;
		case TIMEOFDAY:
			return MetaFieldDataType.TIMEOFDAY;
		default:
			return null;
		}
	}
	
	/**
	 * Converts from the abstract LinkCardinality to the LinkCardinality as defined by WSDL.
	 * @param abstractLinkCardinality The abstract enumeration.
	 * @return The WSDL defined LinkCardinality.
	 */
	private static MetaLinkCardinality convertCardinality(LinkCardinality abstractLinkCardinality) {
		switch (abstractLinkCardinality) {
		case ONE_TO_MANY:
			return MetaLinkCardinality.C_1_M;
		default:
			return null;
		}
	}
	
	/**
	 * Generate the metadata which represents the tables and their columns in the database. 
	 * @return A list of all the Tables the service may access.
	 */
	/*private static List<Table> buildMetadata() {
		
		List<Table> abstractTables = new ArrayList<Table>();
		abstractTables.add(buildProductPseudoTable());
		abstractTables.add(buildCameraTable());
		abstractTables.add(buildCustomerRequestTable());
		abstractTables.add(buildCustomerPhoneTable());
		
		return abstractTables;
	}*/

	/**
	 * Builds the 'product' Table. This table does NOT represent a table in the schema, instead it is used 
	 * to allow OPM to map to a single Global table which then link to a table with multiple rows. 
	 * @return The 'product' Table.
	 */
	/*private static Table buildProductPseudoTable() {
		
		// Fields - none because this table does represent a real table, and is used as a gateway 
		// to the 'camera' table via its link.
		
		// Links
		List<Link> abstractLinks = new ArrayList<Link>();
		abstractLinks.add(new Link(Camera.LINKED_TO_NAME, Camera.OPM_TABLE_NAME, LinkCardinality.ONE_TO_MANY));
		
		// the Product table
		Table productTable = new Table(PRODUCTS_PSEUDO_TABLE_NAME, true, false, null, abstractLinks);
		return productTable;
	}*/
	
	/**
	 * Builds the 'camera' Table.
	 * @return The 'camera' Table.
	 */
	/*private static Table buildCameraTable() {
		
		// Fields
		List<Field> abstractFields = new ArrayList<Field>();
		abstractFields.add(new Field(Camera.COL_MODEL, FieldType.STRING, true, false, false));
		abstractFields.add(new Field(Camera.COL_PRICE, FieldType.DECIMAL, true, false, false));
		abstractFields.add(new Field(Camera.COL_HAS_FLASH, FieldType.BOOLEAN, true, false, false));
		abstractFields.add(new Field(Camera.COL_HAS_PORTRAIT, FieldType.BOOLEAN, true, false, false));
		abstractFields.add(new Field(Camera.COL_HAS_REDEYE, FieldType.BOOLEAN, true, false, false));
		abstractFields.add(new Field(Camera.COL_HAS_TIMER, FieldType.BOOLEAN, true, false, false));
		abstractFields.add(new Field(Camera.COL_COLOR_CODE, FieldType.STRING, true, false, false));
		
		// Links - none because camera falls underneath the 'product' table
		
		// Notice that the table's canBeInput AND canBeOutput are both set to false. This does not mean 
		// the the table can no have values loaded or saved to it, but that this table can not be 
		// selected at the global Mapping Settings level. It is linked to by the 'product' table, 
		// which allows it to have values 'mapped in' when it is a child entity of this table.
		Table cameraTable = new Table(Camera.OPM_TABLE_NAME, false, false, abstractFields, null);
		return cameraTable;
	}*/
	
	/**
	 * Builds the 'customer_request' Table.
	 * @return The 'customer_request' Table.
	 */
	/*private static Table buildCustomerRequestTable() {
		
		// Fields
		List<Field> abstractFields = new ArrayList<Field>();
		abstractFields.add(new Field(CustomerRequest.COL_REQUEST_ID, FieldType.INTEGER, true, false, true));
		// Notice that the request_ID field has canBeInput set to true, while all others are false,
		// this allows OPA to retrieve the ID after a new request has been saved and use it to display to the 
		// user of an interview
		
		abstractFields.add(new Field(CustomerRequest.COL_SELECTED_CAMERA_MODEL, FieldType.STRING, false, true, true));
		abstractFields.add(new Field(CustomerRequest.COL_REQUEST_DATE, FieldType.DATE, false, true, true));
		abstractFields.add(new Field(CustomerRequest.COL_FIRST_NAME, FieldType.STRING, false, true, true));
		abstractFields.add(new Field(CustomerRequest.COL_LAST_NAME, FieldType.STRING, false, true, true));
		abstractFields.add(new Field(CustomerRequest.COL_EMAIL, FieldType.STRING, false, true, true));
		abstractFields.add(new Field(CustomerRequest.COL_COMMENT, FieldType.STRING, false, true, true));
		
		// Links
		List<Link> abstractLinks = new ArrayList<Link>();
		abstractLinks.add(new Link(CustomerPhone.LINKED_TO_NAME, CustomerPhone.OPM_TABLE_NAME, LinkCardinality.ONE_TO_MANY));
		
		// While the schema for camera_company has a foreign key relationship between 'customer_request' and 
		// 'camera', we do not want this relationship revealed to OPM as a Link, because it would then appear 
		// as a mapping option below 'camera'
		
		// By placing "selected_camera_model" as a Field rather than a Link, we get separation of tables, 
		// but any Save requests MUST specify a valid camera key in a customer_request Update to uphold 
		// the foreign key constraint.
		
		Table customerRequestTable = new Table(CustomerRequest.OPM_TABLE_NAME, false, true, abstractFields, abstractLinks);
		return customerRequestTable;
	}*/

	/*private static Table buildCustomerPhoneTable() {

		// Fields
		List<Field> abstractFields = new ArrayList<Field>();
		abstractFields.add(new Field("phone_number", FieldType.STRING, false, true, true));
		abstractFields.add(new Field("phone_type", FieldType.STRING, false, true, true));
		abstractFields.add(new Field("is_preferred", FieldType.BOOLEAN, false, true, true));
		
		// Links - none because there are no 1:M relationships leaving from 'customer_phone'
		
		Table customerPhoneTable = new Table(CustomerPhone.OPM_TABLE_NAME, false, false, abstractFields, null);
		return customerPhoneTable;
	}*/
}
