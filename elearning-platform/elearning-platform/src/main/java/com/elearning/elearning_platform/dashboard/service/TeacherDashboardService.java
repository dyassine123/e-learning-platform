package com.elearning.elearning_platform.dashboard.service;

import com.elearning.elearning_platform.dashboard.dto.*;

import java.util.List;

public interface TeacherDashboardService {
    List<EnrollmentStudentResponse> courseEnrollments(Long actorId, String actorRole, Long courseId);
    CourseStudentLessonProgressResponse courseLessonProgress(Long actorId, String actorRole, Long courseId);
    List<QuizSubmissionSummaryResponse> quizSubmissions(Long actorId, String actorRole, Long quizId);
    QuizSubmissionDetailResponse submissionDetail(Long actorId, String actorRole, Long submissionId);
}