package com.elearning.elearning_platform.enrollment.api;

import com.elearning.elearning_platform.enrollment.dto.EnrollmentResponse;
import com.elearning.elearning_platform.enrollment.service.EnrollmentService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CurrentUser currentUser;

    public EnrollmentController(EnrollmentService enrollmentService, CurrentUser currentUser) {
        this.enrollmentService = enrollmentService;
        this.currentUser = currentUser;
    }

    @PostMapping("/courses/{courseId}")
    public EnrollmentResponse enroll(Authentication auth, @PathVariable Long courseId) {
        return enrollmentService.enroll(currentUser.userId(auth), courseId);
    }

    @DeleteMapping("/courses/{courseId}")
    public void unenroll(Authentication auth, @PathVariable Long courseId) {
        enrollmentService.unenroll(currentUser.userId(auth), courseId);
    }

    @GetMapping("/me")
    public Page<EnrollmentResponse> myEnrollments(Authentication auth, Pageable pageable) {
        return enrollmentService.myEnrollments(currentUser.userId(auth), pageable);
    }

    @GetMapping("/courses/{courseId}")
    public Page<EnrollmentResponse> getCourseEnrollments(Authentication auth, @PathVariable Long courseId, Pageable pageable) {
        return enrollmentService.getEnrollmentsByCourse(currentUser.userId(auth), courseId, pageable);
    }
}