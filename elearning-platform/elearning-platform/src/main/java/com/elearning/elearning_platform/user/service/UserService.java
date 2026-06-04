package com.elearning.elearning_platform.user.service;

import com.elearning.elearning_platform.user.dto.CreateUserRequest;
import com.elearning.elearning_platform.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(CreateUserRequest request);
    UserResponse getById(Long id);
    List<UserResponse> list();

    // Missing methods added
    UserResponse updateProfile(Long userId, com.elearning.elearning_platform.user.dto.UpdateProfileRequest request);
    UserResponse toggleStatus(Long id);
}
