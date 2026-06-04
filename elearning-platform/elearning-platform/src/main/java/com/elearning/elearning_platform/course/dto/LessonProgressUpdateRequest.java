package com.elearning.elearning_platform.course.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LessonProgressUpdateRequest(
    @NotNull Long lessonId,
    @NotNull @Min(0) @Max(100) Integer progressPercent
) {}