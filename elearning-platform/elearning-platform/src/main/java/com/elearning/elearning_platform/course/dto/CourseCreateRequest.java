package com.elearning.elearning_platform.course.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CourseCreateRequest(
    @NotNull Long categoryId,

    @NotBlank @Size(max = 140) String title,
    @NotBlank @Size(max = 1000) String description,

    @NotNull Boolean free,
    BigDecimal price, // validated in service depending on free

    @Size(max = 10) String language,
    @Size(max = 30) String level,

    @Size(max = 500) String thumbnailUrl,
    @Size(max = 500) String previewVideoUrl
) {}