package com.elearning.elearning_platform.course.dto;

import java.time.Instant;

public record CourseSectionResponse(
    Long id,
    Long courseId,
    String title,
    String description,
    int displayOrder,
    Instant createdAt
) {}