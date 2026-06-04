package com.elearning.elearning_platform.auth.service;

import com.elearning.elearning_platform.user.domain.User;

public interface JwtService {
    TokenResult issueToken(User user);
    record TokenResult(String token, java.time.Instant expiresAt) {}
}
