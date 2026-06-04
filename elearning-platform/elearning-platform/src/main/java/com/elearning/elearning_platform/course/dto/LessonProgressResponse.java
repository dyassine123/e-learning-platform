package com.elearning.elearning_platform.course.dto;

import java.time.Instant;

public record LessonProgressResponse(
    Long id,
    Long studentId,
    Long lessonId,
    boolean completed,
    int progressPercent,
    Instant lastSeenAt
) {}