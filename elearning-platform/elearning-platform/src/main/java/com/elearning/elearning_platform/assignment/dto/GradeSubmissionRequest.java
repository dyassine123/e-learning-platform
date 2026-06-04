package com.elearning.elearning_platform.assignment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record GradeSubmissionRequest(
    @Min(0) @Max(100) int grade,
    @Size(max = 1500) String teacherComment
) {}