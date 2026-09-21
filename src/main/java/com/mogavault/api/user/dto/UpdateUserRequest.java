package com.mogavault.api.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 500)
        String avatarUrl,

        String bio
) {}