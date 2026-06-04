package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.dto.CourseSectionCreateRequest;
import com.elearning.elearning_platform.course.dto.CourseSectionResponse;

import java.util.List;

public interface CourseSectionService {
    CourseSectionResponse create(Long actorId, String actorRole, CourseSectionCreateRequest request);
    List<CourseSectionResponse> listByCourse(Long actorId, String actorRole, Long courseId);
}