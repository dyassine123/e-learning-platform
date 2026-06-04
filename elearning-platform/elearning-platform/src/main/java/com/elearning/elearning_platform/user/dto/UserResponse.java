package com.elearning.elearning_platform.user.dto;

import com.elearning.elearning_platform.user.domain.Role;
import java.time.Instant;

public record UserResponse(
    Long id,
    String fullName,
    String email,
    Role role,
    String bio,
    String avatarUrl,
    Instant createdAt,
    boolean enabled
) {}
