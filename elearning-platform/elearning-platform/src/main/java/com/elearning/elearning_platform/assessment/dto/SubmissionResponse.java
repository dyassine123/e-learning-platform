package com.elearning.elearning_platform.assessment.dto;

import java.time.Instant;

public record SubmissionResponse(
    Long submissionId,
    Long quizId,
    int score,
    Instant submittedAt
) {}