package com.qual.store.exceptions;

public class OrderItemNotFoundException extends ShopException{
    public OrderItemNotFoundException(String message) {
        super(message);
    }

    public OrderItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderItemNotFoundException(Throwable cause) {
        super(cause);
    }
}
