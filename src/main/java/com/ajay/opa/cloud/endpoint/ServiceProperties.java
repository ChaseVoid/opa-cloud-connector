package com.ajay.opa.cloud.endpoint;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class reads the properties specified in WEB-INF/classes/service.properties
 * and stores them for access by other classes.
 * @version 2014.12.04
 *
 * Created by ajay on 04/12/14.
 *
 */
public final class ServiceProperties {

    private final static Logger LOGGER = Logger.getLogger(ServiceProperties.class);
    private static String PROPERTY_FILE = "configuration/service.properties";
    private static final Properties PROPERTIES = getProperties(PROPERTY_FILE);

    // PROPERTY NAMES
    public static final String SERVICE_USERNAME = "service_username";
    public static final String SERVICE_PASSWORD = "service_password";

    public static final String DATABASE_TYPE = "database_type";
    public static final String DATABASE_PORT = "database_port";
    public static final String DATABASE_HOST_ADDRESS = "database_host_address";
    public static final String DATABASE_SCHEMA_NAME = "database_schema_name";
    public static final String DATABASE_USERNAME = "database_username";
    public static final String DATABASE_PASSWORD = "database_password";

    /**
     * This method populates PROPERTIES, the java.util.Properties collection which is used
     * by the get() method.
     * @return A Properties object filled with
     */
    private static Properties getProperties(String propertyFile) {
        LOGGER.debug("Loading properties from " + propertyFile);

        Properties properties = new Properties();
        InputStream stream = ServiceProperties.class.getResourceAsStream(PROPERTY_FILE);

        boolean success = false;
        String errorTemplate = null;

        if (stream != null) {
            try {
                properties.load(stream);
                success = true;
            } catch (IOException e) {
                errorTemplate = "Unable to read from properties file '%s'.";
            } catch (IllegalArgumentException e) {
                errorTemplate = "Properties file '%s' is corrupt.";
            }
        } else
            errorTemplate = "Properties file '%s' could not be found.";

        if (!success) {
            String errorMessage = String.format(errorTemplate, PROPERTY_FILE);
            LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        return properties;
    }

    /**
     * This method retrieves the value of a property specified in WEB-INF/classes/service.properties.
     * @param propertyName The name of the property to retrieve the value of.
     * @return The property's value.
     */
    public static String get(String propertyName) {
        return PROPERTIES.getProperty(propertyName);
    }
}