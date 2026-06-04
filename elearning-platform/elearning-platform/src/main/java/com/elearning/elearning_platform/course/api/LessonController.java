package com.elearning.elearning_platform.course.api;

import com.elearning.elearning_platform.course.dto.LessonCreateRequest;
import com.elearning.elearning_platform.course.dto.LessonResponse;
import com.elearning.elearning_platform.course.service.LessonService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final CurrentUser currentUser;

    public LessonController(LessonService lessonService, CurrentUser currentUser) {
        this.lessonService = lessonService;
        this.currentUser = currentUser;
    }

    @GetMapping("/{lessonId}")
    public LessonResponse getById(Authentication auth, @PathVariable Long lessonId) {
        return lessonService.getById(currentUser.userId(auth), currentUser.role(auth), lessonId);
    }
    
    @PostMapping
    public LessonResponse create(Authentication auth, @Valid @RequestBody LessonCreateRequest request) {
        return lessonService.create(currentUser.userId(auth), currentUser.role(auth), request);
    }

    @PostMapping("/{lessonId}/publish")
    public LessonResponse publish(Authentication auth, @PathVariable Long lessonId) {
        return lessonService.publish(currentUser.userId(auth), currentUser.role(auth), lessonId);
    }

    @PostMapping("/{lessonId}/unpublish")
    public LessonResponse unpublish(Authentication auth, @PathVariable Long lessonId) {
        return lessonService.unpublish(currentUser.userId(auth), currentUser.role(auth), lessonId);
    }

    @GetMapping("/section/{sectionId}")
    public List<LessonResponse> listBySection(Authentication auth, @PathVariable Long sectionId) {
        return lessonService.listBySection(currentUser.userId(auth), currentUser.role(auth), sectionId);
    }
}