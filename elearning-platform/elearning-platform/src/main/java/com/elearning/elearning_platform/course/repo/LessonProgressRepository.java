package com.elearning.elearning_platform.course.repo;

import com.elearning.elearning_platform.course.domain.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
  Optional<LessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
  List<LessonProgress> findByStudentId(Long studentId);
  List<LessonProgress> findByStudentIdInAndLessonIdIn(List<Long> studentIds, List<Long> lessonIds);
}