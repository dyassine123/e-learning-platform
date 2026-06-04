package com.elearning.elearning_platform.auth.dto;

import com.elearning.elearning_platform.user.domain.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
    @NotBlank @Size(max = 120) String fullName,
    @NotBlank @Email @Size(max = 180) String email,
    @NotBlank @Size(min = 8, max = 72) String password,
    @NotNull Role role
) {}
