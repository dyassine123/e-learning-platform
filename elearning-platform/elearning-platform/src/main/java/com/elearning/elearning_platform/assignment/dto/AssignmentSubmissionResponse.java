package com.elearning.elearning_platform.assignment.dto;

import java.time.Instant;

public record AssignmentSubmissionResponse(
    Long id,
    Long assignmentId,
    Long studentId,
    String studentFullName,
    String fileUrl,
    String textSubmission,
    Integer grade,
    String teacherComment,
    Instant submittedAt,
    Instant gradedAt
) {}