package com.ajay.opa.cloud.endpoint;

import com.ajay.opa.cloud.ws.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class serves as the entry point for the Web Service.
 * It declares the handlers.xml file via the @HandlerChain annotation, which describes the
 * SoapMessageHandler class to handle security of incoming SOAP requests.
 * It declares OpaDataServicePortType as the endpoint interface via the @WebService annotation,
 * which describes each of the available @WebMethods, their parameters and return types,
 * which are all generated automatically from the WSDL by  the 'wsimport' tool which is packaged
 * with JDK versions 6 and up.
 * This class contains methods to handle of each of these WebMethods.
 * @see <a href="http://docs.oracle.com/javase/6/docs/technotes/tools/share/wsimport.html">wsimport</a>
 * @version 2014.12.04
 *
 * Created by ajay on 04/12/14.
 */
@Endpoint
public class OpaConnectorEndpoint implements OpaDataServicePortType {

    private static final String NAMESPACE_URI = "http://xmlns.oracle.com/policyautomation/hub/12.0/metadata/types";

    private static final Logger LOGGER = Logger.getLogger(OpaConnectorEndpoint.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private DataRepository repository;

    @Autowired
    public OpaConnectorEndpoint(DataRepository repository) {
        this.repository = repository;
    }

    /**
     * This method handles the CheckAliveRequests sent from the hub to check on
     * the status of the Web Service. A simple empty CheckAliveResponse is sent
     * back to notify that the service is online and responding.
     */
    @Override
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "check-alive-request")
    @ResponsePayload
    public CheckAliveResponse checkAlive(@RequestPayload CheckAliveRequest request) {

        LOGGER.debug("OpaConnectorService: CheckAliveRequest made at " + DATE_FORMAT.format(new Date()));
        repository.find("test1");

        return new CheckAliveResponse();
    }


    /**
     * This method handles the GetMetadata operation, returning a set of Metadata
     * described by an abstract layer, which is based upon a limited view of the
     * database structure with only 3 of the tables exposed.
     */
    @Override
    public GetMetadataResponse getMetadata(GetMetadataRequest request) {

        LOGGER.debug("OpaConnectorService: generating GetMetadataResponse for GetMetadata request at " + DATE_FORMAT.format(new Date()));

        GetMetadataResponse metadata = null;//MetadataTool.buildMetadataResponse();
        return metadata;
    }


    /**
     * This method handles the Load operation, requesting data for the fields described
     * in the LoadRequest, and returning those values in a LoadResponse object.
     */
    @Override
    public LoadResponse load(LoadRequest request) throws RequestFault_Exception {

        LOGGER.debug("OpaConnectorService: generating LoadResponse for Load request at " + DATE_FORMAT.format(new Date()));

        if (request.getRequestContext() != null && request.getRequestContext().getParameter().size() != 0) {
            LOGGER.debug("Parameters specified by url on start session:");
            for (RequestContextParam param : request.getRequestContext().getParameter())
                LOGGER.debug("Name: " + param.getName() + ", Value: *****");
        }

        LoadResponse response = null;//LoadTool.processRequest(request);
        return response;
    }

    /**
     * This methods handles the Save operation, expecting only a single SubmitTable entry
     * for 'customer_request' in the request's Tables, because that is the only table exposed
     * via GetMetadata. A customerRequest bean is made from the SaveRequest's only SubmitTable
     * entry, and passes this to an instance of customerRequestSDAOImpl to save the data into
     * the database. The ID of the new row is returned.
     */
    @Override
    public SaveResponse save(SaveRequest request) throws RequestFault_Exception {

        LOGGER.debug("OpaConnectorService: generating SaveResponse for Save request at " + DATE_FORMAT.format(new Date()));

        if (request.getRequestContext() != null && request.getRequestContext().getParameter().size() != 0) {
            LOGGER.debug("Parameters specified by url on start session:");
            for (RequestContextParam param : request.getRequestContext().getParameter())
                LOGGER.debug("Name: " + param.getName() + ", Value: *****");
        }

        SaveResponse response = null;//SaveTool.processRequest(request);
        return response;
    }
}
