package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.domain.Lesson;
import com.elearning.elearning_platform.course.domain.LessonProgress;
import com.elearning.elearning_platform.course.dto.LessonProgressResponse;
import com.elearning.elearning_platform.course.repo.LessonProgressRepository;
import com.elearning.elearning_platform.course.repo.LessonRepository;
import com.elearning.elearning_platform.enrollment.repo.EnrollmentRepository;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.user.domain.Role;
import com.elearning.elearning_platform.user.repo.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LessonProgressServiceImpl implements LessonProgressService {

    private final LessonRepository lessonRepository;
    private final LessonProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public LessonProgressServiceImpl(
        LessonRepository lessonRepository,
        LessonProgressRepository progressRepository,
        UserRepository userRepository,
        EnrollmentRepository enrollmentRepository
    ) {
        this.lessonRepository = lessonRepository;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public LessonProgressResponse updateProgress(Long studentId, Long lessonId, int progressPercent) {
        var student = userRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        if (student.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only STUDENT can update lesson progress.");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFoundException("Lesson not found."));

        Long courseId = lesson.getSection().getCourse().getId();

        boolean enrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId);
        if (!enrolled) {
            throw new ForbiddenException("You must be enrolled in the course before updating lesson progress.");
        }

        LessonProgress progress = progressRepository.findByStudentIdAndLessonId(studentId, lessonId)
            .orElseGet(() -> LessonProgress.start(student, lesson));

        progress.markProgress(progressPercent);

        LessonProgress saved = progressRepository.save(progress);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressResponse> listMyProgress(Long studentId) {
        return progressRepository.findByStudentId(studentId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private LessonProgressResponse toResponse(LessonProgress p) {
        return new LessonProgressResponse(
            p.getId(),
            p.getStudent().getId(),
            p.getLesson().getId(),
            p.isCompleted(),
            p.getProgressPercent(),
            p.getLastSeenAt()
        );
    }
}