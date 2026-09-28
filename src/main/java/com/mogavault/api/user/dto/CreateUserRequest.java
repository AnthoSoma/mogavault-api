package com.mogavault.api.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "validation.user.username.required")
        @Size(min = 3, max = 50, message = "validation.user.username.size")
        String username,

        @NotBlank(message = "validation.user.email.required")
        @Email(message = "validation.user.email.format")
        @Size(max = 255)
        String email,

        @NotBlank(message = "validation.user.password.required")
        @Size(min = 8, message = "validation.user.password.size")
        String password,

        @Size(max = 500)
        String avatarUrl,

        String bio
) {
}