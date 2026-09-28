package com.mogavault.api.user.exception;

import com.mogavault.api.common.exception.ResourceNotFoundException;

import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(UUID id) {
        super("errors.user.not_found_by_id", Map.of("id", id.toString()));
    }

    public UserNotFoundException(String username) {
        super("errors.user.not_found_by_username", Map.of("username", username));
    }
}