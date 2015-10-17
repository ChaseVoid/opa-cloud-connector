package com.ajay.opa.cloud.metadata;


/**
 * This class represents the properties of the WSDL defined GetMetadataResponse Field, 
 * and is used as part of an abstract layer representing part of the 'camera_company' 
 * database's structure.
 */
public final class Field {

	private String name;
	private String spanishDescription;
	private FieldType type;
	private boolean canBeInput;
	private boolean canBeOutput;
	private boolean isRequired;

	public String getName() {
		return name;
	}
	public String getSpanishDescription() {
		return spanishDescription;
	}
	public FieldType getType() {
		return type;
	}
	public boolean isCanBeInput() {
		return canBeInput;
	}
	public boolean isCanBeOutput() {
		return canBeOutput;
	}
	public boolean isRequired() {
		return isRequired;
	}
	
	public Field(String name, String spanishDescription, FieldType type, 
			boolean canBeInput, boolean canBeOutput, boolean isRequired) {
		this.name = name;
		this.spanishDescription = spanishDescription;
		this.type = type;
		this.canBeInput = canBeInput;
		this.canBeOutput = canBeOutput;
		this.isRequired = isRequired;
	}
}
