package com.mogavault.api.user;

import com.mogavault.api.common.exception.ConflictException;

import java.util.Map;

public class UserAlreadyExistsException extends ConflictException {

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

    @Override
    public Map<String, Object> getDetails() {
        return Map.of(
                "field", field,
                "rejectedValue", rejectedValue
        );
    }
}