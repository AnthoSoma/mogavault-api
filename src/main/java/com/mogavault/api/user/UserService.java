package com.mogavault.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserProfileResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find user with id: " + id));
    }

    public UserProfileResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserProfileResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find user with username: " + username));
    }

    public UserProfileResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserProfileResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find user with email: " + email));
    }
}