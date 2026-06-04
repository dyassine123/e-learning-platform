package com.elearning.elearning_platform.dashboard.dto;

import java.time.Instant;

public record EnrollmentStudentResponse(
    Long studentId,
    String fullName,
    String email,
    Instant enrolledAt
) {}