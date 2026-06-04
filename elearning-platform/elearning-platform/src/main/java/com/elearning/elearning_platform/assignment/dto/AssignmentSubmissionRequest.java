package com.elearning.elearning_platform.assignment.dto;

import jakarta.validation.constraints.Size;

public record AssignmentSubmissionRequest(
    @Size(max = 500) String fileUrl,
    @Size(max = 3000) String textSubmission
) {}