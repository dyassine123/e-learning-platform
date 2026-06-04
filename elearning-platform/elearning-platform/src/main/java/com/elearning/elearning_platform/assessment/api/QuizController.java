package com.elearning.elearning_platform.assessment.api;

import com.elearning.elearning_platform.assessment.dto.*;
import com.elearning.elearning_platform.assessment.service.QuizService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final CurrentUser currentUser;

    public QuizController(QuizService quizService, CurrentUser currentUser) {
        this.quizService = quizService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public QuizResponse create(Authentication auth, @Valid @RequestBody QuizCreateRequest request) {
        return quizService.create(currentUser.userId(auth), currentUser.role(auth), request);
    }

    @PostMapping("/{quizId}/publish")
    public QuizResponse publish(Authentication auth, @PathVariable Long quizId) {
        return quizService.publish(currentUser.userId(auth), currentUser.role(auth), quizId);
    }

    @PostMapping("/questions")
    public void addQuestion(Authentication auth, @Valid @RequestBody QuestionCreateRequest request) {
        quizService.addQuestion(currentUser.userId(auth), currentUser.role(auth), request);
    }

    @GetMapping("/course/{courseId}")
    public List<QuizResponse> listForCourse(Authentication auth, @PathVariable Long courseId) {
        return quizService.listForCourse(currentUser.userId(auth), currentUser.role(auth), courseId);
    }

    @GetMapping("/{quizId}/questions")
    public List<QuizQuestionResponse> listQuestions(Authentication auth, @PathVariable Long quizId) {
        return quizService.listQuestions(currentUser.userId(auth), currentUser.role(auth), quizId);
    }
}