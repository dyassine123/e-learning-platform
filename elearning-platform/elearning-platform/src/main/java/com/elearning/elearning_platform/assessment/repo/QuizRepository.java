package com.elearning.elearning_platform.assessment.repo;

import com.elearning.elearning_platform.assessment.domain.Quiz;
import com.elearning.elearning_platform.assessment.domain.QuizStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourse_IdAndStatus(Long courseId, QuizStatus status);
    List<Quiz> findByCourse_Id(Long courseId);
}