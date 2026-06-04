package com.elearning.elearning_platform.review.service;

import com.elearning.elearning_platform.review.dto.*;

import java.util.List;

public interface CourseReviewService {
    ReviewResponse create(Long studentId, String actorRole, Long courseId, ReviewCreateRequest request);
    List<ReviewResponse> listByCourse(Long courseId);
    ReviewSummaryResponse summary(Long courseId);

    // Missing methods added
    ReviewResponse update(Long studentId, Long courseId, ReviewCreateRequest request);
    void delete(Long studentId, Long courseId);
    ReviewResponse getMyReview(Long studentId, Long courseId);
}