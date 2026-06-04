package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    CourseResponse create(Long actorId, String actorRole, CourseCreateRequest req);
    CourseResponse update(Long actorId, String actorRole, Long courseId, CourseUpdateRequest req);

    CourseResponse publish(Long actorId, String actorRole, Long courseId);
    CourseResponse unpublish(Long actorId, String actorRole, Long courseId);
    CourseResponse archive(Long actorId, String actorRole, Long courseId);

    Page<CourseResponse> listPublished(Long categoryId, Pageable pageable);
    Page<CourseResponse> listMine(Long actorId, Pageable pageable);

    CourseResponse getPublishedOrOwned(Long actorId, String actorRole, Long courseId);

    // Missing methods added
    Page<CourseResponse> searchPublished(String keyword, Pageable pageable);
    Page<CourseResponse> listByInstructor(Long instructorId, Pageable pageable);

    CourseResponse submitForApproval(Long actorId, String actorRole, Long courseId);
    Page<CourseResponse> listPendingApproval(Pageable pageable);
    CourseResponse approve(Long courseId);
    CourseResponse reject(Long courseId);
    void delete(Long actorId, String actorRole, Long courseId);
}