package com.aummn.eventbridge.exception;

public class OrderException extends Exception {

    // Default constructor
    public OrderException() {
        super();
    }

    // Constructor with a custom message
    public OrderException(String message) {
        super(message);
    }

    // Constructor with a custom message and a cause
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with a cause
    public OrderException(Throwable cause) {
        super(cause);
    }
}
