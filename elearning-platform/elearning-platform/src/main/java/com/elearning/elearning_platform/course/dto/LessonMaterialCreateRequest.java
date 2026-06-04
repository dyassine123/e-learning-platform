package com.elearning.elearning_platform.course.dto;

import com.elearning.elearning_platform.course.domain.LessonMaterial.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LessonMaterialCreateRequest(
    @NotNull Long lessonId,
    @NotNull MaterialType type,
    @Size(max = 160) String title,
    @NotBlank @Size(max = 2000) String content
) {}