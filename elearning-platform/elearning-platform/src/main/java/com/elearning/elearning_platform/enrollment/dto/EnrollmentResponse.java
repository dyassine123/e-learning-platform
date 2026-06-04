package com.elearning.elearning_platform.enrollment.dto;

import java.time.Instant;

public record EnrollmentResponse(
    Long courseId,
    String courseTitle,
    String courseThumbnailUrl,
    String instructorName,
    Instant enrolledAt
) {}