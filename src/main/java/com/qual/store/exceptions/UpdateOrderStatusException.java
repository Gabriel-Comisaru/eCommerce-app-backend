package com.qual.store.exceptions;

public class UpdateOrderStatusException extends RuntimeException{
    public UpdateOrderStatusException(String message) {
        super(message);
    }

    public UpdateOrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateOrderStatusException(Throwable cause) {
        super(cause);
    }
}
