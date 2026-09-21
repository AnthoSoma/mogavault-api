package com.mogavault.api.user;

public class UserAlreadyExistsException extends RuntimeException {

    private final String field;
    private final String rejectedValue;

    public UserAlreadyExistsException(String field, String rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}