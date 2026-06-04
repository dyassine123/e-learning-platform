package com.elearning.elearning_platform.review.dto;

import java.time.Instant;

public record ReviewResponse(
    Long id,
    Long courseId,
    Long studentId,
    String studentFullName,
    int rating,
    String comment,
    Instant createdAt
) {}