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

    @Transactional
    public UserProfileResponse createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a user with username: " + request.username());
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a user with email: " + request.email());
        }

        // TODO: hacher le mot de passe quand Spring Security sera en place (BCrypt/Argon2)
        String temporaryHash = "{noop}" + request.password();

        User user = new User(
                request.username(),
                request.email(),
                temporaryHash,
                request.avatarUrl(),
                request.bio()
        );

        User savedUser = userRepository.save(user);
        return UserProfileResponse.fromEntity(savedUser);
    }
}