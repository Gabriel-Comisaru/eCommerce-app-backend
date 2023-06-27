package com.qual.store.exceptions;

public class InvalidOrderStatusException extends RuntimeException {
    public InvalidOrderStatusException(String message) {
        super(message);
    }

    public InvalidOrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOrderStatusException(Throwable cause) {
        super(cause);
    }
}
