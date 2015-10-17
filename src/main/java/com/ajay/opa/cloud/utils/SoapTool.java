package com.ajay.opa.cloud.utils;

import org.w3c.dom.Node;

import javax.xml.soap.SOAPException;

/**
 * The SoapTool class provides methods to easily retrieve elements and determine 
 * values of elements from org.w3c.dom.Node(s).
 * @version 2014.05.07
 */
public abstract class SoapTool {
	
	/**
	 * Retrieves the child element of a given name, from the supplied parent node.
	 * @param parentNode The parent node to search.
	 * @param name Name of the child element.
	 * @param isRequired Throw an exception if the child element is not found.
	 * @return The child element with the given name, or null if isRequired=false and the element is not found.
	 * @throws javax.xml.soap.SOAPException When isRequired=true and the child element is not found.
	 */
	public static Node getChildElement(Node parentNode, String name, boolean isRequired) throws SOAPException {
		Node element = parentNode.getFirstChild();
		while (element != null) {
			if (element.getNodeType() == Node.ELEMENT_NODE && element.getLocalName().equals(name))
				break;
			element = element.getNextSibling();
		}
		if (element == null)
			throw new SOAPException(String.format("Child element '%s' not found", name));
		return element;
	}
	
	/**
	 * Get the named element at the same level as the given node.
	 * @param node A node at the level the sibling is to be found at.
	 * @param name The expected name of the sibling element.
	 * @param isRequired Throw an exception if the element is not found.
	 * @return The sibling element of the node, or null if isRequired=false and the element is not found.
	 * @throws javax.xml.soap.SOAPException When isRequired=true and the sibling element is not found.
	 */
	public static Node getSiblingElement(Node node, String name, boolean isRequired) throws SOAPException {
		Node siblingNode = node.getNextSibling();
		while (siblingNode != null) {
			if (siblingNode.getNodeType() == Node.ELEMENT_NODE && siblingNode.getLocalName().equals(name))
				break;
			siblingNode.getNextSibling();
		}
		if (siblingNode == null) {
			siblingNode = node.getPreviousSibling();
			while (siblingNode != null) {
				if (siblingNode.getNodeType() == Node.ELEMENT_NODE && siblingNode.getLocalName().equals(name))
					break;
				siblingNode.getPreviousSibling();
			}
		}
		if (siblingNode == null)
			throw new SOAPException(String.format("Sibling element '%s' not found", name));
		return siblingNode;
	}

	/**
	 * Determines if the node/element has element text AND if that text equals the given value.
	 * @param node The node to examine.
	 * @param value The expected text value of the node.
	 * @return True if element text has the expected value.
	 */
	public static boolean nodeValueEquals(Node node, String value) {
		Node textValue = node.getFirstChild();
		while (textValue != null) {
			if (Node.TEXT_NODE == textValue.getNodeType()
					&& value.equals(textValue.getNodeValue()))
				return true;
			else
				textValue = textValue.getNextSibling();
		}

		return false;
	}
}
