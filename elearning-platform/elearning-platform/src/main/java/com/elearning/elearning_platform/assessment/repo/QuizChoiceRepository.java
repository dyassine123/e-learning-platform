package com.elearning.elearning_platform.assessment.repo;

import com.elearning.elearning_platform.assessment.domain.QuizChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizChoiceRepository extends JpaRepository<QuizChoice, Long> {
    List<QuizChoice> findByQuestion_Id(Long questionId);
    Optional<QuizChoice> findById(Long id);
}