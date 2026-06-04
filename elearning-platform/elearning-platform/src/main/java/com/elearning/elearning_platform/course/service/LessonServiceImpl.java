package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.course.domain.CourseSection;
import com.elearning.elearning_platform.course.domain.Lesson;
import com.elearning.elearning_platform.course.dto.LessonCreateRequest;
import com.elearning.elearning_platform.course.dto.LessonResponse;
import com.elearning.elearning_platform.course.repo.CourseSectionRepository;
import com.elearning.elearning_platform.course.repo.LessonRepository;
import com.elearning.elearning_platform.enrollment.repo.EnrollmentRepository;
import com.elearning.elearning_platform.shared.error.ConflictException;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.user.domain.Role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LessonServiceImpl implements LessonService {

    private final CourseSectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getById(Long actorId, String actorRole, Long lessonId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        Course course = lesson.getSection().getCourse();

        boolean isAdmin = Role.ADMIN.name().equals(actorRole);
        boolean isOwner = course.getOwner().getId().equals(actorId);
        boolean isPublishedLesson = course.getStatus().name().equals("PUBLISHED") && lesson.isPublished();
        boolean isEnrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(actorId, course.getId());

        if (!(isAdmin || isOwner || isEnrolled || isPublishedLesson)) {
            throw new ForbiddenException("You are not allowed to view this lesson.");
        }

        return toResponse(lesson);
    }

    public LessonServiceImpl(
        CourseSectionRepository sectionRepository,
        LessonRepository lessonRepository,
        EnrollmentRepository enrollmentRepository
    ) {
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public LessonResponse create(Long actorId, String actorRole, LessonCreateRequest request) {
        CourseSection section = sectionRepository.findById(request.sectionId())
            .orElseThrow(() -> new NotFoundException("Section not found."));

        assertCanManageCourse(actorId, actorRole, section.getCourse());

        if (lessonRepository.existsBySectionIdAndDisplayOrder(section.getId(), request.displayOrder())) {
            throw new ConflictException("A lesson with this display order already exists in the section.");
        }

        Lesson lesson = Lesson.create(
            section,
            request.title(),
            request.type(),
            request.displayOrder(),
            request.estimatedMinutes()
        );

        return toResponse(lessonRepository.save(lesson));
    }

    @Override
    public LessonResponse publish(Long actorId, String actorRole, Long lessonId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        assertCanManageCourse(actorId, actorRole, lesson.getSection().getCourse());
        lesson.publish();
        return toResponse(lesson);
    }

    @Override
    public LessonResponse unpublish(Long actorId, String actorRole, Long lessonId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        assertCanManageCourse(actorId, actorRole, lesson.getSection().getCourse());
        lesson.unpublish();
        return toResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> listBySection(Long actorId, String actorRole, Long sectionId) {
        CourseSection section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new NotFoundException("Section not found."));

        Course course = section.getCourse();
        boolean isAdmin = Role.ADMIN.name().equals(actorRole);
        boolean isOwner = course.getOwner().getId().equals(actorId);
        boolean isPublished = course.getStatus().name().equals("PUBLISHED");
        boolean isEnrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(actorId, course.getId());

        if (!(isAdmin || isOwner || isPublished || isEnrolled)) {
            throw new ForbiddenException("You are not allowed to view lessons of this section.");
        }

        var lessons = lessonRepository.findBySectionIdOrderByDisplayOrderAsc(sectionId);
        if (isAdmin || isOwner || isEnrolled) {
            return lessons.stream().map(this::toResponse).toList();
        }

        return lessons.stream()
            .filter(Lesson::isPublished)
            .map(this::toResponse)
            .toList();
    }

    private Lesson getLessonOrThrow(Long lessonId) {
        return lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFoundException("Lesson not found."));
    }

    private void assertCanManageCourse(Long actorId, String actorRole, Course course) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        if (Role.TEACHER.name().equals(actorRole) && course.getOwner().getId().equals(actorId)) return;
        throw new ForbiddenException("Only ADMIN or the course owner can manage lessons.");
    }

    private LessonResponse toResponse(Lesson l) {
        return new LessonResponse(
            l.getId(),
            l.getSection().getId(),
            l.getSection().getCourse().getId(),
            l.getTitle(),
            l.getType(),
            l.getDisplayOrder(),
            l.getEstimatedMinutes(),
            l.isPublished(),
            l.getCreatedAt()
        );
    }
}