package com.elearning.elearning_platform.user.dto;

import com.elearning.elearning_platform.user.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank @Size(max = 120) String fullName,
    @NotBlank @Email @Size(max = 180) String email,
    @NotNull Role role
) {}
