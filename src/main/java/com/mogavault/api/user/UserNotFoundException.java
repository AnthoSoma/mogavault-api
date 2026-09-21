package com.mogavault.api.user;

import com.mogavault.api.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(UUID id) {
        super("User with id " + id + " cannot be found");
    }

    public UserNotFoundException(String username) {
        super("User with username " + username + " cannot be found");
    }
}