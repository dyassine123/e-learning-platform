package com.elearning.elearning_platform.course.dto;

import com.elearning.elearning_platform.course.domain.LessonMaterial.MaterialType;
import java.time.Instant;

public record LessonMaterialResponse(
    Long id,
    Long lessonId,
    MaterialType type,
    String title,
    String content,
    Instant createdAt
) {}