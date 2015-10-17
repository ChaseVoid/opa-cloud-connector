package com.ajay.opa.cloud.metadata;

/**
 * This class represents the enumeration types available to the WSDL defined 
 * GetMetadataResponse Link, and is used as part of an abstract layer representing 
 * part of the 'camera_company' database's structure. Link Cardinality describes 
 * the cardinality of a relationship between two  tables.
 */
public enum LinkCardinality {
	ONE_TO_ONE,
	ONE_TO_MANY,
	MANY_TO_ONE,
	MANY_TO_MANY
}
