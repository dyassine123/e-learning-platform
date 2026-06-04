package com.elearning.elearning_platform.review.api;

import com.elearning.elearning_platform.review.dto.*;
import com.elearning.elearning_platform.review.service.CourseReviewService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/reviews")
public class CourseReviewController {

    private final CourseReviewService service;
    private final CurrentUser currentUser;

    public CourseReviewController(CourseReviewService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ReviewResponse create(Authentication auth, @PathVariable Long courseId, @Valid @RequestBody ReviewCreateRequest request) {
        return service.create(currentUser.userId(auth), currentUser.role(auth), courseId, request);
    }

    @GetMapping
    public List<ReviewResponse> list(@PathVariable Long courseId) {
        return service.listByCourse(courseId);
    }

    @GetMapping("/summary")
    public ReviewSummaryResponse summary(@PathVariable Long courseId) {
        return service.summary(courseId);
    }

    @GetMapping("/me")
    public ReviewResponse getMyReview(Authentication auth, @PathVariable Long courseId) {
        return service.getMyReview(currentUser.userId(auth), courseId);
    }

    @PutMapping("/me")
    public ReviewResponse update(Authentication auth, @PathVariable Long courseId, @Valid @RequestBody ReviewCreateRequest request) {
        return service.update(currentUser.userId(auth), courseId, request);
    }

    @DeleteMapping("/me")
    public void delete(Authentication auth, @PathVariable Long courseId) {
        service.delete(currentUser.userId(auth), courseId);
    }
}