package com.elearning.elearning_platform.announcement.dto;

import java.time.Instant;

public record AnnouncementResponse(
    Long id,
    Long courseId,
    Long createdById,
    String title,
    String content,
    Instant createdAt
) {}