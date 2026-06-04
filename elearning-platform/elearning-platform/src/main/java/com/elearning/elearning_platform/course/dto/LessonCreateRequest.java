package com.elearning.elearning_platform.course.dto;

import com.elearning.elearning_platform.course.domain.Lesson.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LessonCreateRequest(
    @NotNull Long sectionId,
    @NotBlank @Size(max = 160) String title,
    @NotNull LessonType type,
    @NotNull Integer displayOrder,
    Integer estimatedMinutes
) {}