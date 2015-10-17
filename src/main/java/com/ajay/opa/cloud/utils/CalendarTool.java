package com.ajay.opa.cloud.utils;

import com.oracle.xmlns.policyautomation.hub._12_0.metadata.types.RequestFault_Exception;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * This class has methods to help convert between different java.sql.* Date, Time, and Timestamp types 
 * and the XMLGregorianCalendar type which XML uses.
 * @version 2014.06.05
 */
public final class CalendarTool {
	
	private final static Logger LOGGER = LogManager.getLogger(CalendarTool.class);
	
	/**
	 * This method takes a java.sql.Date object in which the time and time zone are irrelevant, and uses the 
	 * values to create an XMLGregorianCalendar without a TimeZone.
	 * @param date A java.sql.Date object
	 * @return An XMLGregorianCalendar that will print the date without a time zone suffix.
	 * @throws RequestFault_Exception 
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) throws RequestFault_Exception {
		
		XMLGregorianCalendar xmlCal = null;
		String dateString = date.toString();
		try {
			xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);
		} catch (DatatypeConfigurationException e) {
			ExceptionTool.throwRequestFaultEx("Error.", e.getMessage(), LOGGER, e);
		}
		return xmlCal;
	}
	
	/**
	 * This method takes a java.sql.Time object in which the date and time zone are irrelevant, and uses the 
	 * time values to create an XMLGregorianCalendar without a TimeZone.
	 * @param time java.sql.Time
	 * @return  An XMLGregorianCalendar that will print the time without a time zone suffix.
	 * @throws RequestFault_Exception 
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendar(Time time) throws RequestFault_Exception {
		
		String timeString = time.toString();
		XMLGregorianCalendar xmlCal = null;
		try {
			xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(timeString);
		} catch (DatatypeConfigurationException e) {
			ExceptionTool.throwRequestFaultEx("Error.", e.getMessage(), LOGGER, e);
		}
		return xmlCal;

	}
	
	/**
	 * This method takes a java.sql.Timestamp object which has timezone information, and generates 
	 * a XMLGregorianCalender object with the specified TimeZone (typically from a Load Request). The 
	 * String output of this object will include the timezone matching the request, and without milliseconds, 
	 * as milliseconds are not used by OPA in datetime objects.
	 * @param timestamp
	 * @param requestTimezone
	 * @return A XMLGregorianCalendar which will print the date, time (without milliseconds) and the 
	 * timezone offset.
	 * @throws RequestFault_Exception
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendar(Timestamp timestamp, TimeZone requestTimezone) throws RequestFault_Exception {
		
		GregorianCalendar gregCal = new GregorianCalendar();
		gregCal.setTimeZone(requestTimezone);
		gregCal.setTime(timestamp);
		
		XMLGregorianCalendar xmlCal = null;
		try {
			xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
			
			// setting the Fractional Second to null will avoid printing milliseconds in the output
			xmlCal.setFractionalSecond(null);
			
		} catch (DatatypeConfigurationException e) {
			ExceptionTool.throwRequestFaultEx("Error.", e.getMessage(), LOGGER, e);
		}
		return xmlCal;
	}
	
	/**
	 * This method takes an XMLGregorianCalendar object (from a Save request) and generates a 
	 * java.sql.Date object.
	 * @param xmlCalendar
	 * @return
	 */
	public static Date toSqlDate(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar == null)
			return null;

		return new Date(xmlCalendar.toGregorianCalendar().getTimeInMillis());
	}
	
	/**
	 * This method takes an XMLGregorianCalendar object (from a Save request) and generates a 
	 * java.sql.Time object.
	 * @param xmlCalendar
	 * @return
	 */
	public static Time toSqlTime(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar == null)
			return null;

		return new Time(xmlCalendar.toGregorianCalendar().getTimeInMillis());
	}
	
	/**
	 * This method takes an XMLGregorianCalendar object (from a Save request) and generates a 
	 * java.sql.Timestamp object.
	 * @param xmlCalendar
	 * @return
	 */
	public static Timestamp toSqlTimestamp(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar == null)
			return null;

		return new Timestamp(xmlCalendar.toGregorianCalendar().getTimeInMillis());
	}
}
