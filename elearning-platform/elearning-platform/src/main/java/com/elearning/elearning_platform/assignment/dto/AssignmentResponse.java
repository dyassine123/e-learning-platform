package com.elearning.elearning_platform.assignment.dto;

import com.elearning.elearning_platform.assignment.domain.AssignmentStatus;

import java.time.Instant;

public record AssignmentResponse(
    Long id,
    Long courseId,
    Long createdById,
    String title,
    String description,
    String attachmentUrl,
    Instant dueDate,
    AssignmentStatus status,
    Instant createdAt,
    Instant updatedAt
) {}