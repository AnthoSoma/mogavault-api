package com.mogavault.api.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Username is mandatory")
        @Size(min = 3, max = 50, message = "Username should contains between 3 and 50 characters")
        String username,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password should contain at least 8 characters")
        String password,

        @Size(max = 500)
        String avatarUrl,

        String bio
) {}