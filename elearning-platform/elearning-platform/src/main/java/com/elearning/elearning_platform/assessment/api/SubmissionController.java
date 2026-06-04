package com.elearning.elearning_platform.assessment.api;

import com.elearning.elearning_platform.assessment.dto.SubmissionRequest;
import com.elearning.elearning_platform.assessment.dto.SubmissionResponse;
import com.elearning.elearning_platform.assessment.service.SubmissionService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final CurrentUser currentUser;

    public SubmissionController(SubmissionService submissionService, CurrentUser currentUser) {
        this.submissionService = submissionService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public SubmissionResponse submit(Authentication auth, @Valid @RequestBody SubmissionRequest request) {
        return submissionService.submit(currentUser.userId(auth), currentUser.role(auth), request);
    }

    @GetMapping("/me")
    public java.util.List<SubmissionResponse> getMySubmissions(
        Authentication auth, 
        @RequestParam(required = false) Long courseId
    ) {
        return submissionService.listMySubmissions(currentUser.userId(auth), courseId);
    }
}