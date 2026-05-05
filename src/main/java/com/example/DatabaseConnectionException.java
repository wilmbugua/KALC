package com.example;

/**
 * Custom exception for database connection failures.
 * Provides detailed information about connection issues
 * in the KALCPOS application.
 */
public class DatabaseConnectionException extends Exception {
    
    /**
     * Constructs a new DatabaseConnectionException with the specified detail message.
     * @param message the detail message
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DatabaseConnectionException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method)
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new DatabaseConnectionException with the specified cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method)
     */
    public DatabaseConnectionException(Throwable cause) {
        super(cause);
    }
}