package com.ajay.opa.cloud.utils;

/**
 * Created by asolleti on 10/18/2015.
 */
public class InvalidBase64DataException extends Exception {
    private static final long serialVersionUID = -2123355398144411870L;

    public InvalidBase64DataException(String message) {
        super(message);
    }
}
