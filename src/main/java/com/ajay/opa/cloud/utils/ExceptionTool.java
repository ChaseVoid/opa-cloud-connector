package com.ajay.opa.cloud.utils;

import com.oracle.xmlns.policyautomation.hub._12_0.metadata.types.*;


import org.apache.logging.log4j.Logger;


/**
 * This class serves to throw exceptions in a standardized manner, and similarly use Log4j at the same 
 * time, reducing the amount of code required in other classes. 
 * @version 2014.05.29
 */
public final class ExceptionTool {
	
	/**
	 * This method throws a RequestFault_Exception with the message details passed in, and logs the error 
	 * using the org.apache.log4j.Logger with those same details.
	 * @param title The title of the fault.
	 * @param message The detailed message of why the fault occurred.
	 * @param logger The org.apache.log4j.Logger owned by the Class which caused the fault.
	 * @param cause The throwable exception, if there was one.
	 * @throws RequestFault_Exception The Web Service Connector WSDL defined RequestFault_Exception
	 */
	public static void throwRequestFaultEx(String title, String message, Logger logger, Throwable cause) throws RequestFault_Exception {
		RequestFault info = new RequestFault();
		info.setMessage(message);
		String loggerMessage = title.trim() + " " + message.trim();
		
		if (cause == null) {
			logger.error(loggerMessage);
			throw new RequestFault_Exception(title, info);
		} else {
			logger.error(loggerMessage, cause);
			throw new RequestFault_Exception(title, info, cause);
		}
	}
	
	/**
	 * This method throws a RequestFault_Exception with the message details passed in, and logs the error 
	 * using the org.apache.log4j.Logger with those same details.
	 * @param title The title of the fault.
	 * @param message The detailed message of why the fault occurred.
	 * @param logger The org.apache.log4j.Logger owned by the Class which caused the fault.
	 * @throws RequestFault_Exception The Web Service Connector WSDL defined RequestFault_Exception
	 */
	public static void throwRequestFaultEx(String title, String message, Logger logger) throws RequestFault_Exception {
		throwRequestFaultEx(title, message, logger, null);
	}
}
