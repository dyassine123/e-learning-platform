package com.elearning.elearning_platform.review.service;

import com.elearning.elearning_platform.course.domain.CourseStatus;
import com.elearning.elearning_platform.course.repo.CourseRepository;
import com.elearning.elearning_platform.enrollment.repo.EnrollmentRepository;
import com.elearning.elearning_platform.review.domain.CourseReview;
import com.elearning.elearning_platform.review.dto.*;
import com.elearning.elearning_platform.review.repo.CourseReviewRepository;
import com.elearning.elearning_platform.shared.error.ConflictException;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.user.domain.Role;
import com.elearning.elearning_platform.user.repo.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseReviewRepository reviewRepository;

    public CourseReviewServiceImpl(
        CourseRepository courseRepository,
        UserRepository userRepository,
        EnrollmentRepository enrollmentRepository,
        CourseReviewRepository reviewRepository
    ) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponse create(Long studentId, String actorRole, Long courseId, ReviewCreateRequest request) {
        var student = userRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        if (student.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only STUDENT can review courses.");
        }

        var course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ForbiddenException("You can only review published courses.");
        }

        if (!enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId)) {
            throw new ForbiddenException("You must be enrolled to review this course.");
        }

        if (reviewRepository.existsByStudent_IdAndCourse_Id(studentId, courseId)) {
            throw new ConflictException("You already reviewed this course.");
        }

        CourseReview saved = reviewRepository.save(new CourseReview(course, student, request.rating(), request.comment()));
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> listByCourse(Long courseId) {
        // validate course exists (optional)
        courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found."));
        return reviewRepository.findByCourse_IdOrderByIdDesc(courseId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryResponse summary(Long courseId) {
        courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found."));
        var reviews = reviewRepository.findByCourse_IdOrderByIdDesc(courseId);
        long count = reviews.size();
        if (count == 0) return new ReviewSummaryResponse(0, 0.0);

        double avg = reviews.stream().mapToInt(CourseReview::getRating).average().orElse(0.0);
        return new ReviewSummaryResponse(count, avg);
    }

    private ReviewResponse toResponse(CourseReview r) {
        return new ReviewResponse(
            r.getId(),
            r.getCourse().getId(),
            r.getStudent().getId(),
            r.getStudent().getFullName(),
            r.getRating(),
            r.getComment(),
            r.getCreatedAt()
        );
    }

    @Override
    public ReviewResponse update(Long studentId, Long courseId, ReviewCreateRequest request) {
        CourseReview review = reviewRepository.findByStudent_IdAndCourse_Id(studentId, courseId)
            .orElseThrow(() -> new NotFoundException("Review not found for this course."));
            
        review.update(request.rating(), request.comment());
        return toResponse(review);
    }

    @Override
    public void delete(Long studentId, Long courseId) {
        CourseReview review = reviewRepository.findByStudent_IdAndCourse_Id(studentId, courseId)
            .orElseThrow(() -> new NotFoundException("Review not found for this course."));
            
        reviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getMyReview(Long studentId, Long courseId) {
        CourseReview review = reviewRepository.findByStudent_IdAndCourse_Id(studentId, courseId)
            .orElseThrow(() -> new NotFoundException("Review not found for this course."));
            
        return toResponse(review);
    }
}