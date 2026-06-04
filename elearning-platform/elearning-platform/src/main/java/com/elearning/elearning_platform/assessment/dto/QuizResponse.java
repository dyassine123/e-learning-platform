package com.elearning.elearning_platform.assessment.dto;

import com.elearning.elearning_platform.assessment.domain.QuizStatus;
import java.time.Instant;

public record QuizResponse(
    Long id,
    Long courseId,
    Long createdById,
    String title,
    QuizStatus status,
    Instant createdAt,
    Instant updatedAt
) {}