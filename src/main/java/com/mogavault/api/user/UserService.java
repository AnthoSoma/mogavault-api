package com.mogavault.api.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserProfileResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserProfileResponse::fromEntity)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public UserProfileResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserProfileResponse::fromEntity)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Transactional
    public UserProfileResponse createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UserAlreadyExistsException("username", request.username(), "There is already a user with username: " + request.username());
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistsException("email", request.email(), "There is already a user with email: " + request.email());
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

    @Transactional
    public UserProfileResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl());
        }

        if (request.bio() != null) {
            user.setBio(request.bio());
        }

        return UserProfileResponse.fromEntity(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}