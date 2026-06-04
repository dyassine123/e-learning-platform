package com.elearning.elearning_platform.assessment.service;

import com.elearning.elearning_platform.assessment.domain.*;
import com.elearning.elearning_platform.assessment.dto.SubmissionRequest;
import com.elearning.elearning_platform.assessment.dto.SubmissionResponse;
import com.elearning.elearning_platform.assessment.repo.*;
import com.elearning.elearning_platform.enrollment.repo.EnrollmentRepository;
import com.elearning.elearning_platform.shared.error.ConflictException;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.user.domain.Role;
import com.elearning.elearning_platform.user.repo.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

    private final QuizRepository quizRepo;
    private final QuizQuestionRepository questionRepo;
    private final QuizChoiceRepository choiceRepo;
    private final QuizSubmissionRepository submissionRepo;
    private final QuizAnswerRepository answerRepo;

    private final UserRepository userRepo;
    private final EnrollmentRepository enrollmentRepo;

    public SubmissionServiceImpl(
        QuizRepository quizRepo,
        QuizQuestionRepository questionRepo,
        QuizChoiceRepository choiceRepo,
        QuizSubmissionRepository submissionRepo,
        QuizAnswerRepository answerRepo,
        UserRepository userRepo,
        EnrollmentRepository enrollmentRepo
    ) {
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.choiceRepo = choiceRepo;
        this.submissionRepo = submissionRepo;
        this.answerRepo = answerRepo;
        this.userRepo = userRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    @Override
    public SubmissionResponse submit(Long studentId, String actorRole, SubmissionRequest request) {
        var student = userRepo.findById(studentId).orElseThrow(() -> new NotFoundException("User not found."));
        if (student.getRole() != Role.STUDENT) throw new ForbiddenException("Only STUDENT can submit quizzes.");

        Quiz quiz = quizRepo.findById(request.quizId()).orElseThrow(() -> new NotFoundException("Quiz not found."));
        if (quiz.getStatus() != QuizStatus.PUBLISHED) throw new ForbiddenException("Quiz is not published.");

        Long courseId = quiz.getCourse().getId();
        if (!enrollmentRepo.existsByStudent_IdAndCourse_Id(studentId, courseId)) {
            throw new ForbiddenException("You must be enrolled in the course to submit this quiz.");
        }

        if (submissionRepo.existsByQuiz_IdAndStudent_Id(quiz.getId(), studentId)) {
            throw new ConflictException("You have already submitted this quiz.");
        }

        List<QuizQuestion> questions = questionRepo.findByQuiz_Id(quiz.getId());
        if (questions.isEmpty()) throw new ConflictException("Quiz has no questions.");

        QuizSubmission submission = submissionRepo.save(new QuizSubmission(quiz, student));

        int score = 0;

        for (var ans : request.answers()) {
            QuizQuestion q = questionRepo.findById(ans.questionId())
                .orElseThrow(() -> new NotFoundException("Question not found: " + ans.questionId()));

            if (!q.getQuiz().getId().equals(quiz.getId())) {
                throw new ConflictException("Question does not belong to this quiz.");
            }

            boolean correct = false;

            if (q.getType() == QuestionType.MCQ) {
                if (ans.selectedChoiceId() == null) throw new ConflictException("MCQ requires selectedChoiceId.");
                var choices = choiceRepo.findByQuestion_Id(q.getId());
                var selected = choices.stream()
                    .filter(c -> c.getId().equals(ans.selectedChoiceId()))
                    .findFirst()
                    .orElseThrow(() -> new ConflictException("Selected choice not found for this question."));
                correct = selected.isCorrect();
            } else {
                String expected = q.getCorrectAnswerText();
                String given = ans.answerText();
                if (given == null) given = "";
                // simple exact match (you can improve later)
                correct = expected != null && expected.trim().equalsIgnoreCase(given.trim());
            }

            if (correct) score += q.getPoints();

            answerRepo.save(new QuizAnswer(
                submission,
                q,
                ans.selectedChoiceId(),
                ans.answerText(),
                correct
            ));
        }

        submission.setScore(score);

        return new SubmissionResponse(submission.getId(), quiz.getId(), submission.getScore(), submission.getSubmittedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> listMySubmissions(Long studentId, Long courseId) {
        // Option to filter by a specific course if courseId is provided
        List<QuizSubmission> submissions = submissionRepo.findByStudent_Id(studentId);
        
        return submissions.stream()
            .filter(sub -> courseId == null || sub.getQuiz().getCourse().getId().equals(courseId))
            .map(sub -> new SubmissionResponse(sub.getId(), sub.getQuiz().getId(), sub.getScore(), sub.getSubmittedAt()))
            .toList();
    }
}