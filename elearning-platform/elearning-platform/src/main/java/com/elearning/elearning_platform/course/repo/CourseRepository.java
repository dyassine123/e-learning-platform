package com.elearning.elearning_platform.course.repo;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.course.domain.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    Page<Course> findByOwner_Id(Long ownerId, Pageable pageable);

    Page<Course> findByStatusAndCategory_Id(CourseStatus status, Long categoryId, Pageable pageable);
    
    // Missing methods added
    Page<Course> findByStatusAndTitleContainingIgnoreCase(CourseStatus status, String keyword, Pageable pageable);
    Page<Course> findByStatusAndOwner_Id(CourseStatus status, Long ownerId, Pageable pageable);
}
