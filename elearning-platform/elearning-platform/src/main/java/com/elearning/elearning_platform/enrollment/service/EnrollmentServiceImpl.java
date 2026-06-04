package com.elearning.elearning_platform.enrollment.service;

import com.elearning.elearning_platform.course.domain.CourseStatus;
import com.elearning.elearning_platform.course.repo.CourseRepository;
import com.elearning.elearning_platform.enrollment.domain.Enrollment;
import com.elearning.elearning_platform.enrollment.dto.EnrollmentResponse;
import com.elearning.elearning_platform.enrollment.repo.EnrollmentRepository;
import com.elearning.elearning_platform.shared.error.ConflictException;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.user.domain.Role;
import com.elearning.elearning_platform.user.repo.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentServiceImpl(
        EnrollmentRepository enrollmentRepository,
        CourseRepository courseRepository,
        UserRepository userRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EnrollmentResponse enroll(Long studentId, Long courseId) {
        // Ensure student exists + role is STUDENT
        var student = userRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        if (student.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only STUDENT can enroll.");
        }

        var course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ForbiddenException("Only PUBLISHED courses can be enrolled.");
        }

        if (enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId)) {
            throw new ConflictException("Already enrolled.");
        }

        Enrollment saved = enrollmentRepository.save(new Enrollment(student, course));
        return new EnrollmentResponse(
            saved.getCourse().getId(),
            saved.getCourse().getTitle(),
            saved.getCourse().getThumbnailUrl(),
            saved.getCourse().getOwner().getFullName(),
            saved.getEnrolledAt()
        );
    }

    @Override
    public void unenroll(Long studentId, Long courseId) {
        Enrollment e = enrollmentRepository.findByStudent_IdAndCourse_Id(studentId, courseId)
            .orElseThrow(() -> new NotFoundException("Enrollment not found."));
        enrollmentRepository.delete(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> myEnrollments(Long studentId, Pageable pageable) {
        return enrollmentRepository.findByStudent_Id(studentId, pageable)
            .map(e -> new EnrollmentResponse(
                e.getCourse().getId(),
                e.getCourse().getTitle(),
                e.getCourse().getThumbnailUrl(),
                e.getCourse().getOwner().getFullName(),
                e.getEnrolledAt()
            ));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollmentsByCourse(Long actorId, Long courseId, Pageable pageable) {
        var course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));
            
        // Only the course owner or an admin can view all enrollments
        var actor = userRepository.findById(actorId)
            .orElseThrow(() -> new NotFoundException("User not found."));
            
        if (actor.getRole() != Role.ADMIN && !course.getOwner().getId().equals(actorId)) {
            throw new ForbiddenException("Only ADMIN or course owner can view course enrollments.");
        }

        return enrollmentRepository.findByCourse_Id(courseId, pageable)
            .map(e -> new EnrollmentResponse(
                e.getCourse().getId(),
                e.getCourse().getTitle(),
                e.getCourse().getThumbnailUrl(),
                e.getCourse().getOwner().getFullName(),
                e.getEnrolledAt()
            ));
    }
}