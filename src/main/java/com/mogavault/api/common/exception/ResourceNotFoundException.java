package com.mogavault.api.common.exception;

import java.util.Map;

public abstract class ResourceNotFoundException extends RuntimeException {
    private final String messageKey;
    private final Map<String, Object> messageParams;

    protected ResourceNotFoundException(String messageKey, Map<String, Object> messageParams) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageParams = messageParams != null ? messageParams : Map.of();
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Map<String, Object> getMessageParams() {
        return messageParams;
    }
}