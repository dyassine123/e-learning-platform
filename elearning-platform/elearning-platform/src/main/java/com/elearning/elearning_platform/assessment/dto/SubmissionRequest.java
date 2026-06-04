package com.elearning.elearning_platform.assessment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SubmissionRequest(
    @NotNull Long quizId,
    @NotNull List<AnswerRequest> answers
) {
    public record AnswerRequest(
        @NotNull Long questionId,
        Long selectedChoiceId,
        String answerText
    ) {}
}