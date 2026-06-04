package com.elearning.elearning_platform.dashboard.dto;

import java.time.Instant;

public record QuizSubmissionSummaryResponse(
    Long submissionId,
    Long studentId,
    String studentFullName,
    String studentEmail,
    int score,
    Instant submittedAt
) {}