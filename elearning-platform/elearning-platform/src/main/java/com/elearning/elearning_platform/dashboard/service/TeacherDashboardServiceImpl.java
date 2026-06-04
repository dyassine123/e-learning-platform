package com.elearning.elearning_platform.dashboard.service;

import com.elearning.elearning_platform.assessment.domain.Quiz;
import com.elearning.elearning_platform.assessment.domain.QuizAnswer;
import com.elearning.elearning_platform.assessment.domain.QuizChoice;
import com.elearning.elearning_platform.assessment.domain.QuizSubmission;
import com.elearning.elearning_platform.assessment.repo.QuizAnswerRepository;
import com.elearning.elearning_platform.assessment.repo.QuizChoiceRepository;
import com.elearning.elearning_platform.assessment.repo.QuizRepository;
import com.elearning.elearning_platform.assessment.repo.QuizSubmissionRepository;
import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.course.domain.Lesson;
import com.elearning.elearning_platform.course.repo.CourseSectionRepository;
import com.elearning.elearning_platform.course.repo.CourseRepository;
import com.elearning.elearning_platform.course.repo.LessonProgressRepository;
import com.elearning.elearning_platform.course.repo.LessonRepository;
import com.elearning.elearning_platform.dashboard.dto.*;
import com.elearning.elearning_platform.enrollment.repo.EnrollmentRepository;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.user.domain.Role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class TeacherDashboardServiceImpl implements TeacherDashboardService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseSectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository submissionRepository;
    private final QuizAnswerRepository answerRepository;
    private final QuizChoiceRepository choiceRepository;

    public TeacherDashboardServiceImpl(
        CourseRepository courseRepository,
        EnrollmentRepository enrollmentRepository,
        CourseSectionRepository sectionRepository,
        LessonRepository lessonRepository,
        LessonProgressRepository lessonProgressRepository,
        QuizRepository quizRepository,
        QuizSubmissionRepository submissionRepository,
        QuizAnswerRepository answerRepository,
        QuizChoiceRepository choiceRepository
    ) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.quizRepository = quizRepository;
        this.submissionRepository = submissionRepository;
        this.answerRepository = answerRepository;
        this.choiceRepository = choiceRepository;
    }

    @Override
    public List<EnrollmentStudentResponse> courseEnrollments(Long actorId, String actorRole, Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));

        assertTeacherOrAdminCanAccessCourse(actorId, actorRole, course);

        return enrollmentRepository.findByCourse_Id(courseId).stream()
            .map(e -> new EnrollmentStudentResponse(
                e.getStudent().getId(),
                e.getStudent().getFullName(),
                e.getStudent().getEmail(),
                e.getEnrolledAt()
            ))
            .toList();
    }

    @Override
    public CourseStudentLessonProgressResponse courseLessonProgress(Long actorId, String actorRole, Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));

        assertTeacherOrAdminCanAccessCourse(actorId, actorRole, course);

        var enrollments = enrollmentRepository.findByCourse_Id(courseId);
        var sections = sectionRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
        List<Lesson> lessons = new ArrayList<>();
        sections.forEach(s -> lessons.addAll(lessonRepository.findBySectionIdOrderByDisplayOrderAsc(s.getId())));
        lessons.sort((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()));

        List<Long> studentIds = enrollments.stream().map(e -> e.getStudent().getId()).toList();
        List<Long> lessonIds = lessons.stream().map(Lesson::getId).toList();
        var progressRows = (studentIds.isEmpty() || lessonIds.isEmpty())
            ? List.<com.elearning.elearning_platform.course.domain.LessonProgress>of()
            : lessonProgressRepository.findByStudentIdInAndLessonIdIn(studentIds, lessonIds);

        Map<String, com.elearning.elearning_platform.course.domain.LessonProgress> progressMap = new HashMap<>();
        progressRows.forEach(p -> progressMap.put(p.getStudent().getId() + "-" + p.getLesson().getId(), p));

        List<CourseStudentLessonProgressResponse.StudentProgressRow> students = enrollments.stream().map(e -> {
            var lessonRows = lessons.stream().map(lesson -> {
                var key = e.getStudent().getId() + "-" + lesson.getId();
                var progress = progressMap.get(key);
                int percent = progress != null ? progress.getProgressPercent() : 0;
                boolean completed = progress != null && progress.isCompleted();
                return new CourseStudentLessonProgressResponse.LessonProgressRow(
                    lesson.getId(),
                    lesson.getTitle(),
                    lesson.getDisplayOrder(),
                    percent,
                    completed
                );
            }).toList();

            int completedLessons = (int) lessonRows.stream().filter(CourseStudentLessonProgressResponse.LessonProgressRow::completed).count();
            int totalLessons = lessons.size();
            int overall = totalLessons == 0 ? 0 : Math.round((completedLessons * 100f) / totalLessons);

            return new CourseStudentLessonProgressResponse.StudentProgressRow(
                e.getStudent().getId(),
                e.getStudent().getFullName(),
                e.getStudent().getEmail(),
                overall,
                completedLessons,
                totalLessons,
                lessonRows
            );
        }).toList();

        return new CourseStudentLessonProgressResponse(
            course.getId(),
            course.getTitle(),
            lessons.size(),
            students
        );
    }

    @Override
    public List<QuizSubmissionSummaryResponse> quizSubmissions(Long actorId, String actorRole, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new NotFoundException("Quiz not found."));

        assertTeacherOrAdminCanAccessCourse(actorId, actorRole, quiz.getCourse());

        return submissionRepository.findByQuiz_Id(quizId).stream()
            .map(s -> new QuizSubmissionSummaryResponse(
                s.getId(),
                s.getStudent().getId(),
                s.getStudent().getFullName(),
                s.getStudent().getEmail(),
                s.getScore(),
                s.getSubmittedAt()
            ))
            .toList();
    }

    @Override
    public QuizSubmissionDetailResponse submissionDetail(Long actorId, String actorRole, Long submissionId) {
        QuizSubmission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new NotFoundException("Submission not found."));

        assertTeacherOrAdminCanAccessCourse(actorId, actorRole, submission.getQuiz().getCourse());

        List<QuizAnswer> answers = answerRepository.findBySubmission_Id(submissionId);

        var detailed = answers.stream().map(a -> {
            String selectedChoiceText = null;
            Long choiceId = a.getSelectedChoiceId();
            if (choiceId != null) {
                selectedChoiceText = choiceRepository.findById(choiceId)
                    .map(QuizChoice::getChoiceText)
                    .orElse(null);
            }

            return new QuizSubmissionDetailResponse.AnswerDetail(
                a.getQuestion().getId(),
                a.getQuestion().getType(),
                a.getQuestion().getQuestionText(),
                a.getQuestion().getPoints(),
                a.getSelectedChoiceId(),
                selectedChoiceText,
                a.getAnswerText(),
                a.isCorrect()
            );
        }).toList();

        return new QuizSubmissionDetailResponse(
            submission.getId(),
            submission.getQuiz().getId(),
            submission.getStudent().getId(),
            submission.getStudent().getFullName(),
            submission.getScore(),
            submission.getSubmittedAt(),
            detailed
        );
    }

    private void assertTeacherOrAdminCanAccessCourse(Long actorId, String actorRole, Course course) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        if (Role.TEACHER.name().equals(actorRole) && course.getOwner().getId().equals(actorId)) return;
        throw new ForbiddenException("Only ADMIN or course owner TEACHER can access this data.");
    }
}