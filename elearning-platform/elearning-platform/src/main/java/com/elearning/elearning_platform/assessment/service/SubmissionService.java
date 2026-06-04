package com.elearning.elearning_platform.assessment.service;

import com.elearning.elearning_platform.assessment.dto.SubmissionRequest;
import com.elearning.elearning_platform.assessment.dto.SubmissionResponse;
import java.util.List;

public interface SubmissionService {
    SubmissionResponse submit(Long studentId, String actorRole, SubmissionRequest request);

    // Missing methods added
    List<SubmissionResponse> listMySubmissions(Long studentId, Long courseId);
}