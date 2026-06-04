package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.category.repo.CategoryRepository;
import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.course.domain.CourseStatus;
import com.elearning.elearning_platform.course.dto.*;
import com.elearning.elearning_platform.course.repo.CourseRepository;
import com.elearning.elearning_platform.enrollment.repo.EnrollmentRepository;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.shared.error.ConflictException;
import com.elearning.elearning_platform.user.domain.Role;
import com.elearning.elearning_platform.user.repo.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseServiceImpl(
        CourseRepository courseRepository,
        UserRepository userRepository,
        CategoryRepository categoryRepository,
        EnrollmentRepository enrollmentRepository
    ) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public CourseResponse create(Long actorId, String actorRole, CourseCreateRequest req) {
        if (!isTeacherOrAdmin(actorRole)) throw new ForbiddenException("Only TEACHER/ADMIN can create courses.");

        var owner = userRepository.findById(actorId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        var category = categoryRepository.findById(req.categoryId())
            .orElseThrow(() -> new NotFoundException("Category not found."));

        BusinessFields business = validateBusiness(req.free(), req.price());

        Course course = new Course(
            owner,
            category,
            req.title().trim(),
            req.description().trim(),
            business.free,
            business.price,
            normalize(req.language()),
            normalize(req.level()),
            normalizeUrl(req.thumbnailUrl()),
            normalizeUrl(req.previewVideoUrl())
        );

        return toResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse update(Long actorId, String actorRole, Long courseId, CourseUpdateRequest req) {
        Course course = getCourseOrThrow(courseId);
        assertCanManage(actorId, actorRole, course);

        var category = categoryRepository.findById(req.categoryId())
            .orElseThrow(() -> new NotFoundException("Category not found."));

        BusinessFields business = validateBusiness(req.free(), req.price());

        course.updateContentAndBusiness(
            category,
            req.title().trim(),
            req.description().trim(),
            business.free,
            business.price,
            normalize(req.language()),
            normalize(req.level()),
            normalizeUrl(req.thumbnailUrl()),
            normalizeUrl(req.previewVideoUrl())
        );

        return toResponse(course);
    }

    @Override
    public CourseResponse publish(Long actorId, String actorRole, Long courseId) {
        Course course = getCourseOrThrow(courseId);
        assertAdminCanChangeStatus(actorRole);
        course.publish();
        return toResponse(course);
    }

    @Override
    public CourseResponse unpublish(Long actorId, String actorRole, Long courseId) {
        Course course = getCourseOrThrow(courseId);
        assertAdminCanChangeStatus(actorRole);
        course.unpublishToDraft();
        return toResponse(course);
    }

    @Override
    public CourseResponse archive(Long actorId, String actorRole, Long courseId) {
        Course course = getCourseOrThrow(courseId);
        assertAdminCanChangeStatus(actorRole);
        course.archive();
        return toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> listPublished(Long categoryId, Pageable pageable) {
        if (categoryId == null) {
            return courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable).map(this::toResponse);
        }
        return courseRepository.findByStatusAndCategory_Id(CourseStatus.PUBLISHED, categoryId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> listMine(Long actorId, Pageable pageable) {
        return courseRepository.findByOwner_Id(actorId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchPublished(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return listPublished(null, pageable);
        }
        return courseRepository.findByStatusAndTitleContainingIgnoreCase(CourseStatus.PUBLISHED, keyword.trim(), pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> listByInstructor(Long instructorId, Pageable pageable) {
        return courseRepository.findByStatusAndOwner_Id(CourseStatus.PUBLISHED, instructorId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getPublishedOrOwned(Long actorId, String actorRole, Long courseId) {
        Course course = getCourseOrThrow(courseId);

        boolean isAdmin = Role.ADMIN.name().equals(actorRole);
        boolean isOwner = course.getOwner().getId().equals(actorId);
        boolean isEnrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(actorId, courseId);

        if (course.getStatus() == CourseStatus.PUBLISHED || isAdmin || isOwner || isEnrolled) return toResponse(course);
        throw new ForbiddenException("Not allowed to access this course.");
    }

    @Override
    public CourseResponse submitForApproval(Long actorId, String actorRole, Long courseId) {
        Course course = getCourseOrThrow(courseId);
        assertCanManage(actorId, actorRole, course);
        course.submitForApproval();
        return toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> listPendingApproval(Pageable pageable) {
        return courseRepository.findByStatus(CourseStatus.PENDING_APPROVAL, pageable).map(this::toResponse);
    }

    @Override
    public CourseResponse approve(Long courseId) {
        Course course = getCourseOrThrow(courseId);
        course.publish();
        return toResponse(course);
    }

    @Override
    public CourseResponse reject(Long courseId) {
        Course course = getCourseOrThrow(courseId);
        course.rejectToDraft();
        return toResponse(course);
    }

    @Override
    public void delete(Long actorId, String actorRole, Long courseId) {
        Course course = getCourseOrThrow(courseId);
        assertCanManage(actorId, actorRole, course);
        courseRepository.delete(course);
    }

    // ---------- Helpers ----------

    private Course getCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));
    }

    private boolean isTeacherOrAdmin(String role) {
        return Role.TEACHER.name().equals(role) || Role.ADMIN.name().equals(role);
    }

    private void assertCanManage(Long actorId, String actorRole, Course course) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        if (Role.TEACHER.name().equals(actorRole) && course.getOwner().getId().equals(actorId)) return;
        throw new ForbiddenException("Only ADMIN or course owner TEACHER can manage this course.");
    }

    private void assertAdminCanChangeStatus(String actorRole) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        throw new ForbiddenException("Only ADMIN can change course status.");
    }

    private CourseResponse toResponse(Course c) {
        return new CourseResponse(
            c.getId(),
            c.getOwner().getId(),
            c.getOwner().getFullName(),
            c.getCategory().getId(),
            c.getCategory().getName(),
            c.getTitle(),
            c.getDescription(),
            c.isFree(),
            c.getPrice(),
            c.getLanguage(),
            c.getLevel(),
            c.getThumbnailUrl(),
            c.getPreviewVideoUrl(),
            c.getStatus(),
            c.getCreatedAt(),
            c.getUpdatedAt(),
            c.getPublishedAt()
        );
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    private static String normalizeUrl(String s) {
        // Keep it simple: trim + null if empty.
        return normalize(s);
    }

    private static BusinessFields validateBusiness(Boolean free, BigDecimal price) {
        if (free == null) throw new ConflictException("Field 'free' is required.");
        if (free) {
            // free course: price must be null or zero
            return new BusinessFields(true, null);
        }
        // paid course: must have a positive price
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ConflictException("Paid course must have a positive price.");
        }
        return new BusinessFields(false, price);
    }

    private record BusinessFields(boolean free, BigDecimal price) {}
}
