package com.elearning.elearning_platform.user.dto;

public record UpdateProfileRequest(
    String fullName,
    String bio,
    String avatarUrl
) {}
