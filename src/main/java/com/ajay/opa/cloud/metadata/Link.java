package com.ajay.opa.cloud.metadata;


/**
 * This class represents the properties of the WSDL defined GetMetadataResponse Link, 
 * and is used as part of an abstract layer representing part of the 'camera_company' 
 * database's structure.
 */
public final class Link {

	private String name;
	private String spanishDescription;
	private String target;
	private LinkCardinality cardinality;

	public String getName() {
		return name;
	}
	public String getSpanishDescription() {
		return spanishDescription;
	}
	public String getTarget() {
		return target;
	}
	public LinkCardinality getCardinality() {
		return cardinality;
	}
	
	public Link(String name, String spanishDescription, String target, LinkCardinality cardinality) {
		this.name = name;
		this.spanishDescription = spanishDescription;
		this.target = target;
		this.cardinality = cardinality;
	}
}
