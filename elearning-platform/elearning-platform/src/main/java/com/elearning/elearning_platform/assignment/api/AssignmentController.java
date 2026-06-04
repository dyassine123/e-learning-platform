package com.elearning.elearning_platform.assignment.api;

import com.elearning.elearning_platform.assignment.dto.*;
import com.elearning.elearning_platform.assignment.service.AssignmentService;
import com.elearning.elearning_platform.shared.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CurrentUser currentUser;

    public AssignmentController(AssignmentService assignmentService, CurrentUser currentUser) {
        this.assignmentService = assignmentService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public AssignmentResponse create(Authentication auth, @Valid @RequestBody AssignmentCreateRequest request) {
        return assignmentService.create(currentUser.userId(auth), currentUser.role(auth), request);
    }

    @PostMapping("/{assignmentId}/publish")
    public AssignmentResponse publish(Authentication auth, @PathVariable Long assignmentId) {
        return assignmentService.publish(currentUser.userId(auth), currentUser.role(auth), assignmentId);
    }

    @GetMapping("/course/{courseId}")
    public List<AssignmentResponse> listForCourse(Authentication auth, @PathVariable Long courseId) {
        return assignmentService.listForCourse(currentUser.userId(auth), currentUser.role(auth), courseId);
    }

    @PostMapping("/{assignmentId}/submit")
    public AssignmentSubmissionResponse submit(Authentication auth, @PathVariable Long assignmentId,
                                               @Valid @RequestBody AssignmentSubmissionRequest request) {
        return assignmentService.submit(currentUser.userId(auth), currentUser.role(auth), assignmentId, request);
    }

    @GetMapping("/{assignmentId}/submissions")
    public List<AssignmentSubmissionResponse> listSubmissions(Authentication auth, @PathVariable Long assignmentId) {
        return assignmentService.listSubmissions(currentUser.userId(auth), currentUser.role(auth), assignmentId);
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public AssignmentSubmissionResponse grade(Authentication auth, @PathVariable Long submissionId,
                                              @Valid @RequestBody GradeSubmissionRequest request) {
        return assignmentService.grade(currentUser.userId(auth), currentUser.role(auth), submissionId, request);
    }

    @GetMapping("/submissions/me")
    public List<AssignmentSubmissionResponse> listMySubmissions(
        Authentication auth, 
        @RequestParam(required = false) Long courseId
    ) {
        return assignmentService.listMySubmissions(currentUser.userId(auth), courseId);
    }

    @GetMapping("/course/{courseId}/submissions/pending")
    public List<AssignmentSubmissionResponse> listPendingSubmissions(Authentication auth, @PathVariable Long courseId) {
        return assignmentService.listPendingSubmissions(currentUser.userId(auth), currentUser.role(auth), courseId);
    }
}