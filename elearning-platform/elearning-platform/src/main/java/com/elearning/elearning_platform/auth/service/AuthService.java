package com.elearning.elearning_platform.auth.service;

import com.elearning.elearning_platform.auth.dto.*;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
