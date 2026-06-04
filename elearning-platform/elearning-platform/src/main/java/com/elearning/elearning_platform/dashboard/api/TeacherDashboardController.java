package com.elearning.elearning_platform.dashboard.api;

import com.elearning.elearning_platform.dashboard.dto.*;
import com.elearning.elearning_platform.dashboard.service.TeacherDashboardService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class TeacherDashboardController {

    private final TeacherDashboardService service;
    private final CurrentUser currentUser;

    public TeacherDashboardController(TeacherDashboardService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping("/courses/{courseId}/enrollments")
    public List<EnrollmentStudentResponse> courseEnrollments(Authentication auth, @PathVariable Long courseId) {
        return service.courseEnrollments(currentUser.userId(auth), currentUser.role(auth), courseId);
    }

    @GetMapping("/courses/{courseId}/lesson-progress")
    public CourseStudentLessonProgressResponse courseLessonProgress(Authentication auth, @PathVariable Long courseId) {
        return service.courseLessonProgress(currentUser.userId(auth), currentUser.role(auth), courseId);
    }

    @GetMapping("/quizzes/{quizId}/submissions")
    public List<QuizSubmissionSummaryResponse> quizSubmissions(Authentication auth, @PathVariable Long quizId) {
        return service.quizSubmissions(currentUser.userId(auth), currentUser.role(auth), quizId);
    }

    @GetMapping("/submissions/{submissionId}")
    public QuizSubmissionDetailResponse submissionDetail(Authentication auth, @PathVariable Long submissionId) {
        return service.submissionDetail(currentUser.userId(auth), currentUser.role(auth), submissionId);
    }
}