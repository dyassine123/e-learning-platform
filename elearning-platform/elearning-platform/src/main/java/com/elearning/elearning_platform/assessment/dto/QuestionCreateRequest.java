package com.elearning.elearning_platform.assessment.dto;

import com.elearning.elearning_platform.assessment.domain.QuestionType;
import jakarta.validation.constraints.*;

import java.util.List;

public record QuestionCreateRequest(
    @NotNull Long quizId,
    @NotNull QuestionType type,
    @NotBlank @Size(max = 1200) String questionText,
    @Min(1) @Max(100) int points,

    // For MCQ: provide choices, with exactly one correct = true (enforced in service)
    List<ChoiceCreateRequest> choices,

    // For TRUE_FALSE or SHORT_ANSWER: expected answer
    @Size(max = 800) String correctAnswerText
) {
    public record ChoiceCreateRequest(
        @NotBlank @Size(max = 400) String choiceText,
        @NotNull Boolean correct
    ) {}
}