package com.elearning.elearning_platform.course.api;

import com.elearning.elearning_platform.course.dto.LessonProgressResponse;
import com.elearning.elearning_platform.course.dto.LessonProgressUpdateRequest;
import com.elearning.elearning_platform.course.service.LessonProgressService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-progress")
public class LessonProgressController {

    private final LessonProgressService progressService;
    private final CurrentUser currentUser;

    public LessonProgressController(LessonProgressService progressService, CurrentUser currentUser) {
        this.progressService = progressService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public LessonProgressResponse updateProgress(Authentication auth, @Valid @RequestBody LessonProgressUpdateRequest request) {
        return progressService.updateProgress(
            currentUser.userId(auth),
            request.lessonId(),
            request.progressPercent()
        );
    }

    @GetMapping("/me")
    public List<LessonProgressResponse> myProgress(Authentication auth) {
        return progressService.listMyProgress(currentUser.userId(auth));
    }
}