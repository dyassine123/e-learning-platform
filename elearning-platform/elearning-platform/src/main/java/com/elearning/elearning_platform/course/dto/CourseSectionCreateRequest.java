package com.elearning.elearning_platform.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseSectionCreateRequest(
    @NotNull Long courseId,
    @NotBlank @Size(max = 140) String title,
    @Size(max = 600) String description,
    @NotNull Integer displayOrder
) {}