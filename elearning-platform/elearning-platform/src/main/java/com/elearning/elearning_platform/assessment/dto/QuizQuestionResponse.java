package com.elearning.elearning_platform.assessment.dto;

import com.elearning.elearning_platform.assessment.domain.QuestionType;
import java.util.List;

public record QuizQuestionResponse(
    Long id,
    Long quizId,
    QuestionType type,
    String questionText,
    int points,
    String correctAnswerText,
    List<ChoiceResponse> choices
) {
    public record ChoiceResponse(
        Long id,
        String choiceText,
        Boolean correct
    ) {}
}
