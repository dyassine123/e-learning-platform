package com.elearning.elearning_platform.course.dto;

import com.elearning.elearning_platform.course.domain.Lesson.LessonType;
import java.time.Instant;

public record LessonResponse(
    Long id,
    Long sectionId,
    Long courseId,
    String title,
    LessonType type,
    int displayOrder,
    Integer estimatedMinutes,
    boolean published,
    Instant createdAt
) {}