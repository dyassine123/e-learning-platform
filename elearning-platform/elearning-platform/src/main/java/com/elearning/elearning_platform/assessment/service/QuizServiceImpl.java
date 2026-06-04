package com.elearning.elearning_platform.assessment.service;

import com.elearning.elearning_platform.assessment.domain.*;
import com.elearning.elearning_platform.assessment.dto.*;
import com.elearning.elearning_platform.assessment.repo.*;
import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.course.repo.CourseRepository;
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
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepo;
    private final QuizQuestionRepository questionRepo;
    private final QuizChoiceRepository choiceRepo;

    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final EnrollmentRepository enrollmentRepo;

    public QuizServiceImpl(
        QuizRepository quizRepo,
        QuizQuestionRepository questionRepo,
        QuizChoiceRepository choiceRepo,
        CourseRepository courseRepo,
        UserRepository userRepo,
        EnrollmentRepository enrollmentRepo
    ) {
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.choiceRepo = choiceRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    @Override
    public QuizResponse create(Long actorId, String actorRole, QuizCreateRequest request) {
        Course course = courseRepo.findById(request.courseId())
            .orElseThrow(() -> new NotFoundException("Course not found."));

        assertCanManageCourse(actorId, actorRole, course);

        var creator = userRepo.findById(actorId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        Quiz quiz = new Quiz(course, creator, request.title());
        return toResponse(quizRepo.save(quiz));
    }

    @Override
    public QuizResponse publish(Long actorId, String actorRole, Long quizId) {
        Quiz quiz = quizRepo.findById(quizId).orElseThrow(() -> new NotFoundException("Quiz not found."));
        assertCanManageCourse(actorId, actorRole, quiz.getCourse());

        // must have at least 1 question
        if (questionRepo.findByQuiz_Id(quizId).isEmpty()) {
            throw new ConflictException("Cannot publish quiz without questions.");
        }

        quiz.publish();
        return toResponse(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> listForCourse(Long actorId, String actorRole, Long courseId) {
        // Students see only published. Owners/admin see all.
        var course = courseRepo.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found."));
        boolean admin = Role.ADMIN.name().equals(actorRole);
        boolean owner = course.getOwner().getId().equals(actorId);

        List<Quiz> quizzes = (admin || owner)
            ? quizRepo.findByCourse_Id(courseId)
            : quizRepo.findByCourse_IdAndStatus(courseId, QuizStatus.PUBLISHED);

        return quizzes.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizQuestionResponse> listQuestions(Long actorId, String actorRole, Long quizId) {
        Quiz quiz = quizRepo.findById(quizId).orElseThrow(() -> new NotFoundException("Quiz not found."));
        Course course = quiz.getCourse();

        boolean admin = Role.ADMIN.name().equals(actorRole);
        boolean owner = course.getOwner().getId().equals(actorId);
        boolean enrolledStudent = Role.STUDENT.name().equals(actorRole)
            && enrollmentRepo.existsByStudent_IdAndCourse_Id(actorId, course.getId());
        boolean canViewAsStudent = quiz.getStatus() == QuizStatus.PUBLISHED && enrolledStudent;

        if (!(admin || owner || canViewAsStudent)) {
            throw new ForbiddenException("You are not allowed to view quiz questions.");
        }

        boolean revealAnswers = admin || owner;
        return questionRepo.findByQuiz_Id(quizId).stream().map(question -> {
            var choices = choiceRepo.findByQuestion_Id(question.getId()).stream().map(choice ->
                new QuizQuestionResponse.ChoiceResponse(
                    choice.getId(),
                    choice.getChoiceText(),
                    revealAnswers ? choice.isCorrect() : null
                )
            ).toList();

            return new QuizQuestionResponse(
                question.getId(),
                quiz.getId(),
                question.getType(),
                question.getQuestionText(),
                question.getPoints(),
                revealAnswers ? question.getCorrectAnswerText() : null,
                choices
            );
        }).toList();
    }

    @Override
    public void addQuestion(Long actorId, String actorRole, QuestionCreateRequest request) {
        Quiz quiz = quizRepo.findById(request.quizId()).orElseThrow(() -> new NotFoundException("Quiz not found."));
        assertCanManageCourse(actorId, actorRole, quiz.getCourse());

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new ConflictException("You can only edit questions while quiz is DRAFT.");
        }

        QuizQuestion q = new QuizQuestion(
            quiz,
            request.type(),
            request.questionText(),
            request.points(),
            request.correctAnswerText()
        );
        QuizQuestion saved = questionRepo.save(q);

        if (request.type() == QuestionType.MCQ) {
            if (request.choices() == null || request.choices().size() < 2) {
                throw new ConflictException("MCQ must have at least 2 choices.");
            }
            long correctCount = request.choices().stream().filter(c -> Boolean.TRUE.equals(c.correct())).count();
            if (correctCount != 1) {
                throw new ConflictException("MCQ must have exactly 1 correct choice.");
            }
            for (var c : request.choices()) {
                choiceRepo.save(new QuizChoice(saved, c.choiceText(), c.correct()));
            }
        } else {
            // for TRUE_FALSE / SHORT_ANSWER we require correctAnswerText
            if (request.correctAnswerText() == null || request.correctAnswerText().trim().isEmpty()) {
                throw new ConflictException("This question type requires correctAnswerText.");
            }
        }
    }

    private void assertCanManageCourse(Long actorId, String actorRole, Course course) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        if (Role.TEACHER.name().equals(actorRole) && course.getOwner().getId().equals(actorId)) return;
        throw new ForbiddenException("Only ADMIN or course owner TEACHER can manage quizzes.");
    }

    private QuizResponse toResponse(Quiz q) {
        return new QuizResponse(
            q.getId(),
            q.getCourse().getId(),
            q.getCreatedBy().getId(),
            q.getTitle(),
            q.getStatus(),
            q.getCreatedAt(),
            q.getUpdatedAt()
        );
    }
}