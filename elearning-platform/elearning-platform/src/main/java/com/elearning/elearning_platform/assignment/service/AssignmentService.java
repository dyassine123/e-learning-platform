package com.elearning.elearning_platform.assignment.service;

import com.elearning.elearning_platform.assignment.dto.*;

import java.util.List;

public interface AssignmentService {
    AssignmentResponse create(Long actorId, String actorRole, AssignmentCreateRequest request);
    AssignmentResponse publish(Long actorId, String actorRole, Long assignmentId);
    List<AssignmentResponse> listForCourse(Long actorId, String actorRole, Long courseId);

    AssignmentSubmissionResponse submit(Long studentId, String actorRole, Long assignmentId, AssignmentSubmissionRequest request);
    List<AssignmentSubmissionResponse> listSubmissions(Long actorId, String actorRole, Long assignmentId);
    AssignmentSubmissionResponse grade(Long actorId, String actorRole, Long submissionId, GradeSubmissionRequest request);

    // Missing methods added
    List<AssignmentSubmissionResponse> listMySubmissions(Long studentId, Long courseId);
    List<AssignmentSubmissionResponse> listPendingSubmissions(Long actorId, String actorRole, Long courseId);
}