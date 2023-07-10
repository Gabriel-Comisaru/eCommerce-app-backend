package com.qual.store.exceptions;

public class DeleteProductException extends RuntimeException {
    public DeleteProductException(String message) {
        super(message);
    }

    public DeleteProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteProductException(Throwable cause) {
        super(cause);
    }
}
