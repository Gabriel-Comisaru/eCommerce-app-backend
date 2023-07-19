package com.qual.store.exceptions;

public class UpdateOrderStatusException extends RuntimeException{
    public UpdateOrderStatusException(String message) {
        super(message);
    }
}
