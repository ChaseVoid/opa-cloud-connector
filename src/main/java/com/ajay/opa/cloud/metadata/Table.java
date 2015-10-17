package com.ajay.opa.cloud.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the properties of the WSDL defined GetMetadataResponse Table, 
 * and is used as part of an abstract layer representing part of the 'camera_company' 
 * database's structure.
 */
public final class Table {

	private String name;
	private String spanishDescription;
	private boolean canBeInput;
	private boolean canBeOutput;
	private List<Field> fields;
	private List<Link> links;

	public String getName() {
		return name;
	}
	public String getSpanishDescription() {
		return spanishDescription;
	}
	public boolean isCanBeInput() {
		return canBeInput;
	}
	public boolean isCanBeOutput() {
		return canBeOutput;
	}
	public List<Field> getFields() {
		return fields;
	}
	public List<Link> getLinks() {
		return links;
	}

	public Table(String name, String spanishDescription, boolean canBeInput, 
			boolean canBeOutput, List<Field> fields, List<Link> links) {
		this.name = name;
		this.spanishDescription = spanishDescription;
		this.canBeInput = canBeInput;
		this.canBeOutput = canBeOutput;
		this.fields = fields != null ? fields : new ArrayList<>();
		this.links = links != null ? links : new ArrayList<>();
	}
}
