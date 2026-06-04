package com.elearning.elearning_platform.course.repo;

import com.elearning.elearning_platform.course.domain.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
  List<CourseSection> findByCourseIdOrderByDisplayOrderAsc(Long courseId);
  boolean existsByCourseIdAndDisplayOrder(Long courseId, int displayOrder);
}