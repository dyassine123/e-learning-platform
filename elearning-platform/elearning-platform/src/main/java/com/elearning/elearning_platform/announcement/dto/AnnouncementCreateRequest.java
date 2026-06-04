package com.elearning.elearning_platform.announcement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnnouncementCreateRequest(
    @NotNull Long courseId,
    @NotBlank @Size(max = 180) String title,
    @NotBlank @Size(max = 3000) String content
) {}