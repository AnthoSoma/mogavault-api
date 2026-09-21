package com.mogavault.api.common.exception;

import java.util.Collections;
import java.util.Map;

public abstract class ConflictException extends RuntimeException {
    protected ConflictException(String message) {
        super(message);
    }

    public Map<String, Object> getDetails() {
        return Collections.emptyMap();
    }
}