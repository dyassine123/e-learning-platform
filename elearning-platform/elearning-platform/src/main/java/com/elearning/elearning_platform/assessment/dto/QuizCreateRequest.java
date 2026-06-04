package com.elearning.elearning_platform.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuizCreateRequest(
    @NotNull Long courseId,
    @NotBlank @Size(max = 160) String title
) {}