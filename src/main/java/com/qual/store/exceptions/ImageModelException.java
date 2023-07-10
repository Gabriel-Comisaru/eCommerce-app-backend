package com.qual.store.exceptions;

public class ImageModelException extends RuntimeException {
    public ImageModelException(String message) {
        super(message);
    }

    public ImageModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageModelException(Throwable cause) {
        super(cause);
    }
}
