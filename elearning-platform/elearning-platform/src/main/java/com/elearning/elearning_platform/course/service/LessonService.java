package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.dto.LessonCreateRequest;
import com.elearning.elearning_platform.course.dto.LessonResponse;

import java.util.List;

public interface LessonService {
    LessonResponse getById(Long actorId, String actorRole, Long lessonId);
    LessonResponse create(Long actorId, String actorRole, LessonCreateRequest request);
    LessonResponse publish(Long actorId, String actorRole, Long lessonId);
    LessonResponse unpublish(Long actorId, String actorRole, Long lessonId);
    List<LessonResponse> listBySection(Long actorId, String actorRole, Long sectionId);
}