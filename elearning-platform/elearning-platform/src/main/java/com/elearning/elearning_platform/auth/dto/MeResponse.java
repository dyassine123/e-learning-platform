package com.elearning.elearning_platform.auth.dto;

import com.elearning.elearning_platform.user.domain.Role;

public record MeResponse(
    Long id,
    String email,
    Role role
) {}
