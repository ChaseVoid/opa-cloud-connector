package com.ajay.opa.cloud.utils;

import com.oracle.xmlns.policyautomation.hub._12_0.metadata.types.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * This class provides methods to save out a SaveRequest to a database.
 *
 * @version 2014.05.12
 */
public class SaveTool {

    private final static Logger LOGGER = LogManager.getLogger(SaveTool.class);

    private final static CustomerRequestDAOImpl CUSTOMER_REQUEST_DAO = new CustomerRequestDAOImpl();

    /**
     * This method takes a SaveRequest, generates a customerRequest object from it,
     * then passes this to a DAO which insert the data into a database. If this succeeds,
     * the generated ID of the new row is returned.
     *
     * @param request
     * @return
     * @throws RequestFault_Exception
     */
    public static SaveResponse processRequest(SaveRequest request) throws RequestFault_Exception {

        SubmitData requestData = request.getTables();
        List<SubmitTable> requestTables = requestData.getTable();

        /*CustomerRequest customerRequest = null;
        List<CustomerPhone> phoneNumbers = new ArrayList<CustomerPhone>();
        RequestAttachment attachment = null;

        for (SubmitTable reqTable : requestTables) {
            if (reqTable.getName().equals(CustomerPhone.OPM_TABLE_NAME)) {
                phoneNumbers = createCustomerPhoneObject(reqTable);
            } else if (reqTable.getName().equals(CustomerRequest.OPM_TABLE_NAME)) {
                customerRequest = createCustomerRequestObject(reqTable);
            } else {
                ExceptionTool.throwRequestFaultEx("Unexpected table.",
                                                  String.format("The table '%s' was not expected by this service.", reqTable.getName()), LOGGER);
            }
        }

        if (customerRequest == null) {
            ExceptionTool.throwRequestFaultEx("Missing table.", String.format(
                    "'%s' is an expected table in the request.", CustomerRequest.OPM_TABLE_NAME), LOGGER);
        }

        attachment = createRequestAttachment(request);
        customerRequest.setPhoneNumbers(phoneNumbers);
        customerRequest.setAttachment(attachment);
        CUSTOMER_REQUEST_DAO.upsertCustomerRequest(customerRequest);

        SaveResponse response = generateResponse(requestTables, customerRequest);
        return response;*/

        return null;
    }


    /**
     * This method generates a customerRequestObject from the first row of a 'customer_request' table.
     *
     * @param customerRequestTable A table from a SaveRequest.
     * @return A customerRequest object with fields matching values from the request.
     * @throws RequestFault_Exception
     */
    /*private static CustomerRequest createCustomerRequestObject(SubmitTable customerRequestTable) throws RequestFault_Exception {

        if (!CustomerRequest.OPM_TABLE_NAME.equals(customerRequestTable.getName()))
            ExceptionTool.throwRequestFaultEx("Web Service error.", String.format(
                    "A table other than '%s' was passed to the createCustomerRequestObject function.",
                    CustomerRequest.OPM_TABLE_NAME), LOGGER);
        else if (customerRequestTable.getRow().size() != 1)
            ExceptionTool.throwRequestFaultEx("Global table error.", String.format(
                    "Request must only contain one row in the '%s' table as this should be mapped at the global level.",
                    CustomerRequest.OPM_TABLE_NAME), LOGGER);

        CustomerRequest customerRequest = new CustomerRequest();
        SubmitRow requestRow = customerRequestTable.getRow().get(0);

        HashMap<String, Object> valueMap = getInputFieldValuesMap(requestRow.getInputField());

        // this next statement is very important! It makes the difference between an insert
        // or update query in CustomerRequestDAOImpl.upsertCustomerRequest(CustomerRequest)
        if (requestRow.getAction().equals(RowAction.UPDATE))
            customerRequest.setRequestID(Integer.parseInt(requestRow.getId()));

        customerRequest.setEmail((String) valueMap.get(CustomerRequest.COL_EMAIL));
        customerRequest.setFirstName((String) valueMap.get(CustomerRequest.COL_FIRST_NAME));
        customerRequest.setLastName((String) valueMap.get(CustomerRequest.COL_LAST_NAME));
        customerRequest.setCameraModel((String) valueMap.get(CustomerRequest.COL_SELECTED_CAMERA_MODEL));
        customerRequest.setComment((String) valueMap.get(CustomerRequest.COL_COMMENT));

    	Date requestDate = CalendarTool.toSqlDate((XMLGregorianCalendar)valueMap.get(CustomerRequest.COL_REQUEST_DATE));
    	customerRequest.setRequestDate(requestDate);
    	
        return customerRequest;
    }*/

    /**
     * This method takes a 'customer_phone' SubmitTable and generate a List of CustomerPhone beans.
     *
     * @param customerPhoneTable The table to use data from to generate CustomerPhone beans.
     * @return The List of CustomerPhone beans.
     */
    /*private static List<CustomerPhone> createCustomerPhoneObject(SubmitTable customerPhoneTable) {

        if (customerPhoneTable == null || !CustomerPhone.OPM_TABLE_NAME.equals(customerPhoneTable.getName()))
            return null;

        List<CustomerPhone> phoneNumberList = new ArrayList<CustomerPhone>();

        for (SubmitRow phoneRow : customerPhoneTable.getRow()) {

            // because we delete all the phone records linked to the customer request, we treat create
            // and update similarly. This is not the case for the customer request itself though, where
            // 'create' and 'update' do change which sql statement is used
            if (!phoneRow.getAction().equals(RowAction.DELETE)) {
                HashMap<String, Object> valueMap = getInputFieldValuesMap(phoneRow.getInputField());

                CustomerPhone phoneNumber = new CustomerPhone();
                phoneNumber.setIsPreferred((Boolean) valueMap.get(CustomerPhone.COL_IS_PREFERRED));
                phoneNumber.setPhoneNumber((String) valueMap.get(CustomerPhone.COL_PHONE_NUMBER));
                phoneNumber.setPhoneType((String) valueMap.get(CustomerPhone.COL_PHONE_TYPE));
                // we don't set the request ID, as this is a foreign key linked to the customer_request
                // object. SQL queries will insert the value of the parent CustomerRequest's requestID
                // into customer_phone.request_ID instead.

                phoneNumberList.add(phoneNumber);
            }
        }

        return phoneNumberList;
    }*/

    /**
     * This method builds a HashMap of values from the InputField List of a SaveRequest table's row.
     *
     * @param inputFields List of DataFields
     * @return A HashMap of the values of a row's fields.
     */
    private static HashMap<String, Object> getInputFieldValuesMap(List<DataField> inputFields) {

        if (inputFields == null)
            return null;

        HashMap<String, Object> valueMap = new HashMap<>(inputFields.size());

        for (DataField field : inputFields) {
            Object value = null;
            if (field.getUncertainVal() != null
                    || field.getUnknownVal() != null)
                value = null;
            else {
                if (value == null)
                    value = field.getTextVal();
                if (value == null)
                    value = field.getDatetimeVal();
                if (value == null)
                    value = field.getDateVal();
                if (value == null)
                    value = field.getTimeVal();
                if (value == null)
                    value = field.getNumberVal();
                if (value == null)
                    value = field.isBooleanVal();
            }
            valueMap.put(field.getName(), value);
        }

        return valueMap;
    }

    /**
     * This method creates an abstract RequestAttachment from the first item of a save request's Attachment list.
     *
     * @param request The list of Attachments
     * @return The first Attachment as a RequestAttachment, or null if there is none.
     * @throws RequestFault_Exception If there is more than one attachment.
     */
    /*private static RequestAttachment createRequestAttachment(SaveRequest request) throws RequestFault_Exception {

        RequestAttachment attachment = null;

        List<Attachment> attachmentList = request.getAttachments().getAttachment();

        if (attachmentList != null && attachmentList.size() > 0) {
            if (attachmentList.size() > 1)
                ExceptionTool.throwRequestFaultEx("Too many attachments.",
                                                  "This service expects only a single attachment for a request.", LOGGER);

            Attachment firstAttachment = attachmentList.get(0);
            attachment = new RequestAttachment();
            attachment.setFilename(firstAttachment.getFilename());
            // if the attachment is an OPM Form generated by the Interview then it will also carry the attributes
            // 'name' and 'description', describing the name of the form (not the filename) as specified in OPM, and 
            // a description with the value "Form auto generated by OPA.". We do not use these values in this example.
            
            String value = firstAttachment.getValue();
            byte[] blob = javax.xml.bind.DatatypeConverter.parseBase64Binary(value);
            attachment.setBlob(blob);
        }

        return attachment;
    }*/

    /**
     * This method creates a SaveResponse object, filled with the tables which are present in the
     * SaveRequest tables. Any fields in the OutputField list of a request table are filled with the
     * values from the customerRequest, which were added/modified by the CustomerRequestDAOImpl.
     *
     * @param requestTables   The List of SubmitTables from a SaveRequest.
     * @param customerRequest The customerRequest post-DAO upserted.
     * @return The instantiated SaveResponse to return to the caller.
     * @throws RequestFault_Exception
     * @see CustomerRequestDAOImpl
     */
    /*private static SaveResponse generateResponse(
            List<SubmitTable> requestTables, CustomerRequest customerRequest) throws RequestFault_Exception {

        SaveResponse response = new SaveResponse();
        UpdateData responseData = new UpdateData();
        response.setTables(responseData);

        List<UpdateTable> responseTableList = responseData.getTable();

        for (SubmitTable requestTable : requestTables) {

            String tableName = requestTable.getName();
            UpdateTable responseTable = new UpdateTable();
            responseTable.setName(tableName);
            completeResponseTableRows(responseTable, requestTable.getRow(), customerRequest);

            responseTableList.add(responseTable);
        }

        return response;
    }*/

    /*private static void completeResponseTableRows(UpdateTable responseTable,
                                                  List<SubmitRow> requestRowList, CustomerRequest customerRequest) throws RequestFault_Exception {

        String tableName = responseTable.getName();

        List<UpdateRow> responseRowList = responseTable.getRow();

        for (SubmitRow requestRow : requestRowList) {
            UpdateRow responseRow = new UpdateRow();
            responseRow.setId(requestRow.getId());

            if (requestRow.getAction().equals(RowAction.CREATE) && tableName.equals(CustomerRequest.OPM_TABLE_NAME)) {
                responseRow.setOrigId(requestRow.getId());
                responseRow.setId(customerRequest.getRequestID().toString());
            }

            List<DataField> outputFieldList = requestRow.getOutputField();
            List<DataField> responseFieldList = responseRow.getField();
            completeExpectedOutputFields(customerRequest, tableName, responseFieldList, outputFieldList);

            responseRowList.add(responseRow);
        }
    }*/


    /*private static void completeExpectedOutputFields(
            CustomerRequest customerRequest, String tableName, List<DataField> responseFieldList, List<DataField> outputFieldList)
            throws RequestFault_Exception {

        int outputCount = outputFieldList.size();

        if (outputCount > 0) {
            if (tableName.equals(CustomerRequest.OPM_TABLE_NAME) && outputCount == 1) {
                if (outputFieldList.get(0).getName().equals(CustomerRequest.COL_REQUEST_ID)) {

                    DataField outputField = new DataField();
                    outputField.setName(CustomerRequest.COL_REQUEST_ID);
                    outputField.setNumberVal(new BigDecimal(customerRequest.getRequestID()));
                    responseFieldList.add(outputField);
                } else {
                    ExceptionTool.throwRequestFaultEx("Unexpected OutputField.", String.format(
                            "The table '%s' was not expected to have '%s' an OutputField.", tableName, outputCount), LOGGER);
                }

            } else {
                ExceptionTool.throwRequestFaultEx("Unexpected OutputFields.", String.format(
                        "The table '%s' was not expected to have '%s' OutputFields.", tableName, outputCount), LOGGER);
            }
        }
    }*/
}
