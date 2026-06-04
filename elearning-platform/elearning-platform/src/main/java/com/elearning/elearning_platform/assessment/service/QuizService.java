package com.elearning.elearning_platform.assessment.service;

import com.elearning.elearning_platform.assessment.dto.*;

import java.util.List;

public interface QuizService {
    QuizResponse create(Long actorId, String actorRole, QuizCreateRequest request);
    QuizResponse publish(Long actorId, String actorRole, Long quizId);
    List<QuizResponse> listForCourse(Long actorId, String actorRole, Long courseId);
    List<QuizQuestionResponse> listQuestions(Long actorId, String actorRole, Long quizId);

    void addQuestion(Long actorId, String actorRole, QuestionCreateRequest request);
}