package com.group7.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("Không tìm thấy %s với ID: %s", resourceName, resourceId));
    }
}
