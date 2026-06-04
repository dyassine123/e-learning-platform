package com.elearning.elearning_platform.auth.dto;

import java.time.Instant;

public record AuthResponse(
    String tokenType,
    String accessToken,
    Instant expiresAt
) {}
