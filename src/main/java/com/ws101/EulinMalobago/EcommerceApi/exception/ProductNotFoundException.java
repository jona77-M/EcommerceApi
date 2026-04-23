package com.ws101.EulinMalobago.EcommerceApi.exception;

/**
 * Thrown when a requested product cannot be found in the catalog.
 *
 * @author eulin and malobago bsit -2b
 * This exception is translated into a 404 Not Found response by the global
 * exception handler.
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with a descriptive not found message.
     *
     * @param message the message describing the missing product
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}
