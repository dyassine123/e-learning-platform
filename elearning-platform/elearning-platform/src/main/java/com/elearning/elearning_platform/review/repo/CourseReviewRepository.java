package com.elearning.elearning_platform.review.repo;

import com.elearning.elearning_platform.review.domain.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    java.util.Optional<CourseReview> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    List<CourseReview> findByCourse_IdOrderByIdDesc(Long courseId);

    long countByCourse_Id(Long courseId);
}