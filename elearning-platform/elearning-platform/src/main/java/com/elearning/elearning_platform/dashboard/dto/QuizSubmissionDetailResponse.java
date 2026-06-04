package com.elearning.elearning_platform.dashboard.dto;

import com.elearning.elearning_platform.assessment.domain.QuestionType;

import java.time.Instant;
import java.util.List;

public record QuizSubmissionDetailResponse(
    Long submissionId,
    Long quizId,
    Long studentId,
    String studentFullName,
    int score,
    Instant submittedAt,
    List<AnswerDetail> answers
) {
    public record AnswerDetail(
        Long questionId,
        QuestionType type,
        String questionText,
        Integer points,
        Long selectedChoiceId,
        String selectedChoiceText,
        String answerText,
        boolean correct
    ) {}
}