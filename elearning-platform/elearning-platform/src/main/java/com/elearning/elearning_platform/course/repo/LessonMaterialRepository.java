package com.elearning.elearning_platform.course.repo;

import com.elearning.elearning_platform.course.domain.LessonMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonMaterialRepository extends JpaRepository<LessonMaterial, Long> {
  List<LessonMaterial> findByLessonIdOrderByIdAsc(Long lessonId);
}