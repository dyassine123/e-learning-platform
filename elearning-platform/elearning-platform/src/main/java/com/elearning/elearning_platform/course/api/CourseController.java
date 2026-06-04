package com.elearning.elearning_platform.course.api;

import com.elearning.elearning_platform.course.dto.*;
import com.elearning.elearning_platform.course.service.CourseService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final CurrentUser currentUser;

    public CourseController(CourseService courseService, CurrentUser currentUser) {
        this.courseService = courseService;
        this.currentUser = currentUser;
    }

    // GET /api/courses?categoryId=3
    @GetMapping
    public Page<CourseResponse> listPublished(
        @RequestParam(required = false) Long categoryId,
        Pageable pageable
    ) {
        return courseService.listPublished(categoryId, pageable);
    }

    // GET /api/courses/search?keyword=java
    @GetMapping("/search")
    public Page<CourseResponse> searchPublished(
        @RequestParam String keyword,
        Pageable pageable
    ) {
        return courseService.searchPublished(keyword, pageable);
    }

    // GET /api/courses/instructor/5
    @GetMapping("/instructor/{instructorId}")
    public Page<CourseResponse> listByInstructor(
        @PathVariable Long instructorId,
        Pageable pageable
    ) {
        return courseService.listByInstructor(instructorId, pageable);
    }

    @GetMapping("/{id}")
    public CourseResponse get(Authentication auth, @PathVariable Long id) {
        return courseService.getPublishedOrOwned(currentUser.userId(auth), currentUser.role(auth), id);
    }

    @PostMapping
    public CourseResponse create(Authentication auth, @Valid @RequestBody CourseCreateRequest req) {
        return courseService.create(currentUser.userId(auth), currentUser.role(auth), req);
    }

    @PutMapping("/{id}")
    public CourseResponse update(Authentication auth, @PathVariable Long id, @Valid @RequestBody CourseUpdateRequest req) {
        return courseService.update(currentUser.userId(auth), currentUser.role(auth), id, req);
    }

    @PostMapping("/{id}/publish")
    public CourseResponse publish(Authentication auth, @PathVariable Long id) {
        return courseService.publish(currentUser.userId(auth), currentUser.role(auth), id);
    }

    @PostMapping("/{id}/unpublish")
    public CourseResponse unpublish(Authentication auth, @PathVariable Long id) {
        return courseService.unpublish(currentUser.userId(auth), currentUser.role(auth), id);
    }

    @PostMapping("/{id}/archive")
    public CourseResponse archive(Authentication auth, @PathVariable Long id) {
        return courseService.archive(currentUser.userId(auth), currentUser.role(auth), id);
    }

    @GetMapping("/me")
    public Page<CourseResponse> myCourses(Authentication auth, Pageable pageable) {
        return courseService.listMine(currentUser.userId(auth), pageable);
    }

    @PostMapping("/{id}/submit")
    public CourseResponse submit(Authentication auth, @PathVariable Long id) {
        return courseService.submitForApproval(currentUser.userId(auth), currentUser.role(auth), id);
    }

    @GetMapping("/pending")
    public Page<CourseResponse> listPending(Pageable pageable) {
        return courseService.listPendingApproval(pageable);
    }

    @PostMapping("/{id}/approve")
    public CourseResponse approve(@PathVariable Long id) {
        return courseService.approve(id);
    }

    @PostMapping("/{id}/reject")
    public CourseResponse reject(@PathVariable Long id) {
        return courseService.reject(id);
    }

    @DeleteMapping("/{id}")
    public void delete(Authentication auth, @PathVariable Long id) {
        courseService.delete(currentUser.userId(auth), currentUser.role(auth), id);
    }
}