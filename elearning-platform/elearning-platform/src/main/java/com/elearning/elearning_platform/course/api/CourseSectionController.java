package com.elearning.elearning_platform.course.api;

import com.elearning.elearning_platform.course.dto.CourseSectionCreateRequest;
import com.elearning.elearning_platform.course.dto.CourseSectionResponse;
import com.elearning.elearning_platform.course.service.CourseSectionService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
public class CourseSectionController {

    private final CourseSectionService sectionService;
    private final CurrentUser currentUser;

    public CourseSectionController(CourseSectionService sectionService, CurrentUser currentUser) {
        this.sectionService = sectionService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public CourseSectionResponse create(Authentication auth, @Valid @RequestBody CourseSectionCreateRequest request) {
        return sectionService.create(currentUser.userId(auth), currentUser.role(auth), request);
    }

    @GetMapping("/course/{courseId}")
    public List<CourseSectionResponse> listByCourse(Authentication auth, @PathVariable Long courseId) {
        return sectionService.listByCourse(currentUser.userId(auth), currentUser.role(auth), courseId);
    }
}