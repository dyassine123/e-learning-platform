package com.elearning.elearning_platform.course.dto;

import com.elearning.elearning_platform.course.domain.CourseStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record CourseResponse(
    Long id,
    Long ownerId,
    String instructorName,

    Long categoryId,
    String categoryName,

    String title,
    String description,

    boolean free,
    BigDecimal price,
    String language,
    String level,
    String thumbnailUrl,
    String previewVideoUrl,

    CourseStatus status,
    Instant createdAt,
    Instant updatedAt,
    Instant publishedAt
) {}