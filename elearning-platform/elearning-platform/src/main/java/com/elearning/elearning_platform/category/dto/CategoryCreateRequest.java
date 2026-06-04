package com.elearning.elearning_platform.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
    @NotBlank @Size(max = 80) String name,
    @Size(max = 300) String description
) {}