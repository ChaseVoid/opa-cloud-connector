package com.ajay.opa.cloud.endpoint;

import com.ajay.opa.cloud.utils.SoapTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.HashSet;
import java.util.Set;

/**
 * This class handles SOAP messages going to and from the application, and 
 * verifies that all incoming messages contain a security header with valid 
 * user credentials.
 * @version 2014.05.07
 *
 */
public class SoapMessageHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Logger logger = LogManager.getLogger(SoapMessageHandler.class);
	
	private static final String SERVICE_USERNAME= ServiceProperties.get(ServiceProperties.SERVICE_USERNAME);
	private static final String SERVICE_PASSWORD = ServiceProperties.get(ServiceProperties.SERVICE_PASSWORD);

	/**
	 * All in and outbound messages go via this handler. Inbound messages need to 
	 * be verified to contain a valid username and password, otherwise the message 
	 * should not be processed.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		
		Boolean success = false;
		Boolean directionOutbound = ((Boolean)context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY));
		
		if (directionOutbound) {
			success = true;
		} else {
			success = checkCredentials(context);
			String successMessage = "username/password " + (success ? "PASSED" : "FAILED");
			
			if (success)
				logger.debug(successMessage);
			else {
				logger.error(successMessage);
				throw new WebServiceException(successMessage);
			}
		}
		
		return success;
	}

	/**
	 * This method validates the security header in the SOAP message; if the header exists 
	 * and is valid, it checks whether the credentials supplied match the expected values.
	 * @param context The SOAPMessageContext
	 * @return True if credentials pass, fail if not, or throw WebService exception if security header is 
	 * missing or misunderstood.
	 */
	private Boolean checkCredentials(SOAPMessageContext context) {
		Boolean success = false;
		try {
			SOAPHeader soapHeader = context.getMessage().getSOAPHeader();
			Node securityNode = soapHeader.getElementsByTagNameNS(
					"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security").item(0);
			if (securityNode == null)
				throw new WebServiceException("Security element from header missing");
			
			Node usernameToken = SoapTool.getChildElement(securityNode, "UsernameToken", true);
			Node username = SoapTool.getChildElement(usernameToken, "Username", true);
			Node password = SoapTool.getChildElement(usernameToken, "Password", true);

			success = SoapTool.nodeValueEquals(username, SERVICE_USERNAME) &&
					SoapTool.nodeValueEquals(password, SERVICE_PASSWORD);

		} catch (DOMException | SOAPException e) {
			System.err.println();
			logger.error(e.getMessage());
			throw new WebServiceException("Security not understood: " + e.getMessage());
		}
		
		return success;
	}
	
	@PostConstruct
	public void afterInit() {
		// handler initialization complete
	}
	
	@PreDestroy
	public void beforeDestroy() {
		// handler is about to be destroyed
	}
	
	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// handling a fault
		return true;
	}

	@Override
	public void close(MessageContext context) {
		// closing handler
	}

	@Override
	public Set<QName> getHeaders() {
		final QName securityHeader = new QName(
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", 
				"Security", 
				"wsse");
		
		// this understands the request
		final HashSet<QName> headers = new HashSet<>();
		headers.add(securityHeader);
		
		// return handled headers to runtime
		return headers;
	}

}
