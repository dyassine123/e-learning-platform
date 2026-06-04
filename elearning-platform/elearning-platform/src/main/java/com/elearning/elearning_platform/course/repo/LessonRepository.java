package com.elearning.elearning_platform.course.repo;

import com.elearning.elearning_platform.course.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
  List<Lesson> findBySectionIdOrderByDisplayOrderAsc(Long sectionId);
  boolean existsBySectionIdAndDisplayOrder(Long sectionId, int displayOrder);
}