package com.elearning.elearning_platform.assessment.repo;

import com.elearning.elearning_platform.assessment.domain.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findBySubmission_Id(Long submissionId);
}