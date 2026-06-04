package com.elearning.elearning_platform.assessment.repo;

import com.elearning.elearning_platform.assessment.domain.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByStudent_Id(Long studentId);
    List<QuizSubmission> findByQuiz_Id(Long quizId);
    boolean existsByQuiz_IdAndStudent_Id(Long quizId, Long studentId);
}