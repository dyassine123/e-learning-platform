package com.elearning.elearning_platform.enrollment.service;

import com.elearning.elearning_platform.enrollment.dto.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {
    EnrollmentResponse enroll(Long studentId, Long courseId);
    void unenroll(Long studentId, Long courseId);
    Page<EnrollmentResponse> myEnrollments(Long studentId, Pageable pageable);

    // Missing methods added
    Page<EnrollmentResponse> getEnrollmentsByCourse(Long actorId, Long courseId, Pageable pageable);
}