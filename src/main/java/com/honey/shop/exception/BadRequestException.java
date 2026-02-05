package com.honey.shop.exception;

/**
 * Thrown when a request is invalid due to business rules (e.g. insufficient stock).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
