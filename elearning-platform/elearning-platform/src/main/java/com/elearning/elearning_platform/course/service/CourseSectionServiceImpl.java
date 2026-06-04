package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.course.domain.CourseSection;
import com.elearning.elearning_platform.course.dto.CourseSectionCreateRequest;
import com.elearning.elearning_platform.course.dto.CourseSectionResponse;
import com.elearning.elearning_platform.course.repo.CourseRepository;
import com.elearning.elearning_platform.course.repo.CourseSectionRepository;
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
public class CourseSectionServiceImpl implements CourseSectionService {

    private final CourseRepository courseRepository;
    private final CourseSectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseSectionServiceImpl(
        CourseRepository courseRepository,
        CourseSectionRepository sectionRepository,
        EnrollmentRepository enrollmentRepository
    ) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public CourseSectionResponse create(Long actorId, String actorRole, CourseSectionCreateRequest request) {
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new NotFoundException("Course not found."));

        assertCanManageCourse(actorId, actorRole, course);

        if (sectionRepository.existsByCourseIdAndDisplayOrder(course.getId(), request.displayOrder())) {
            throw new ConflictException("A section with this display order already exists in the course.");
        }

        CourseSection section = CourseSection.create(
            course,
            request.title(),
            request.description(),
            request.displayOrder()
        );

        return toResponse(sectionRepository.save(section));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponse> listByCourse(Long actorId, String actorRole, Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));

        boolean isAdmin = Role.ADMIN.name().equals(actorRole);
        boolean isOwner = course.getOwner().getId().equals(actorId);
        boolean isPublished = course.getStatus().name().equals("PUBLISHED");
        boolean isEnrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(actorId, courseId);

        if (!(isAdmin || isOwner || isPublished || isEnrolled)) {
            throw new ForbiddenException("You are not allowed to view sections of this course.");
        }

        return sectionRepository.findByCourseIdOrderByDisplayOrderAsc(courseId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private void assertCanManageCourse(Long actorId, String actorRole, Course course) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        if (Role.TEACHER.name().equals(actorRole) && course.getOwner().getId().equals(actorId)) return;
        throw new ForbiddenException("Only ADMIN or the course owner can manage sections.");
    }

    private CourseSectionResponse toResponse(CourseSection s) {
        return new CourseSectionResponse(
            s.getId(),
            s.getCourse().getId(),
            s.getTitle(),
            s.getDescription(),
            s.getDisplayOrder(),
            s.getCreatedAt()
        );
    }
}