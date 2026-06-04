package com.elearning.elearning_platform.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record AssignmentCreateRequest(
    @NotNull Long courseId,
    @NotBlank @Size(max = 180) String title,
    @NotBlank @Size(max = 3000) String description,
    @Size(max = 500) String attachmentUrl,
    Instant dueDate
) {}