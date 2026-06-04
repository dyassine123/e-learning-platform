package com.elearning.elearning_platform.user.mapper;

import com.elearning.elearning_platform.user.domain.User;
import com.elearning.elearning_platform.user.dto.UserResponse;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user, boolean enabled) {
        return new UserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            user.getBio(),
            user.getAvatarUrl(),
            user.getCreatedAt(),
            enabled
        );
    }
}
