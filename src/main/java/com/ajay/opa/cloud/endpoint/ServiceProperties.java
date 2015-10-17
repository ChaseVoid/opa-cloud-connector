package com.ajay.opa.cloud.endpoint;


import com.ajay.opa.cloud.utils.InvalidBase64DataException;
import com.ajay.opa.cloud.utils.ServicePropertiesEncrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.IllegalBlockSizeException;
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

    private final static Logger log = LogManager.getLogger(ServiceProperties.class);
    private static String PROPERTY_FILE = "configuration/service.properties";
    private static final Properties PROPERTIES = getProperties(PROPERTY_FILE);

    // PROPERTY NAMES
    public static final String SERVICE_USERNAME = "service.username";
    public static final String SERVICE_PASSWORD = "service.password";

    public static final String SFDC_USERNAME = "sfdc.username";
    public static final String SFDC_PASSWORD = "sfdc.password";

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
    public static Properties getProperties(String propertyFile) {
        log.debug("Loading properties from " + propertyFile);

        Properties properties = new Properties();
        InputStream stream = ServiceProperties.class.getResourceAsStream(propertyFile);

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
            String errorMessage = String.format(errorTemplate, propertyFile);
            log.error(errorMessage);
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

        String property = PROPERTIES.getProperty(propertyName);

        if (propertyName.equals(SERVICE_USERNAME)
                || propertyName.equals(SERVICE_PASSWORD)
                || propertyName.equals(SFDC_USERNAME)
                || propertyName.equals(SFDC_PASSWORD)) {
            if (!ServicePropertiesEncrypter.isEncrypted(property))
                log.warn("Username stored in service properties file is not encrypted. Run encryption tool on file to remedy this.");
            else {
                try {
                    property = ServicePropertiesEncrypter.decrypt(property);
                } catch (IllegalBlockSizeException | InvalidBase64DataException e) {
                    log.error("Cannot decrypt username.");
                }
            }
        }

        return property;
    }
}