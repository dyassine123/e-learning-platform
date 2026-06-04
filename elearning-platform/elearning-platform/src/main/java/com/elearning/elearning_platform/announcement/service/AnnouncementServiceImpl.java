package com.elearning.elearning_platform.announcement.service;

import com.elearning.elearning_platform.announcement.domain.Announcement;
import com.elearning.elearning_platform.announcement.dto.*;
import com.elearning.elearning_platform.announcement.repo.AnnouncementRepository;
import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.course.domain.CourseStatus;
import com.elearning.elearning_platform.course.repo.CourseRepository;
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
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AnnouncementServiceImpl(
        AnnouncementRepository announcementRepository,
        CourseRepository courseRepository,
        UserRepository userRepository,
        EnrollmentRepository enrollmentRepository
    ) {
        this.announcementRepository = announcementRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public AnnouncementResponse create(Long actorId, String actorRole, AnnouncementCreateRequest request) {
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new NotFoundException("Course not found."));

        assertTeacherOrAdmin(actorId, actorRole, course);

        var creator = userRepository.findById(actorId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        Announcement saved = announcementRepository.save(
            new Announcement(course, creator, request.title(), request.content())
        );

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> listForCourse(Long actorId, String actorRole, Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));

        boolean admin = Role.ADMIN.name().equals(actorRole);
        boolean owner = course.getOwner().getId().equals(actorId);
        boolean enrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(actorId, courseId);

        if (!(admin || owner || enrolled || course.getStatus() == CourseStatus.PUBLISHED)) {
            throw new ForbiddenException("You are not allowed to see announcements of this course.");
        }

        return announcementRepository.findByCourse_IdOrderByIdDesc(courseId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> listMyAnnouncements(Long studentId) {
        // Find all courses the student is enrolled in
        List<Long> enrolledCourseIds = enrollmentRepository.findByStudent_Id(studentId, org.springframework.data.domain.Pageable.unpaged())
            .stream()
            .map(e -> e.getCourse().getId())
            .toList();

        if (enrolledCourseIds.isEmpty()) {
            return List.of();
        }

        // Fetch announcements for all those courses
        return announcementRepository.findByCourse_IdInOrderByIdDesc(enrolledCourseIds)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private void assertTeacherOrAdmin(Long actorId, String actorRole, Course course) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        if (Role.TEACHER.name().equals(actorRole) && course.getOwner().getId().equals(actorId)) return;
        throw new ForbiddenException("Only ADMIN or course owner TEACHER can create announcements.");
    }

    private AnnouncementResponse toResponse(Announcement a) {
        return new AnnouncementResponse(
            a.getId(),
            a.getCourse().getId(),
            a.getCreatedBy().getId(),
            a.getTitle(),
            a.getContent(),
            a.getCreatedAt()
        );
    }
}