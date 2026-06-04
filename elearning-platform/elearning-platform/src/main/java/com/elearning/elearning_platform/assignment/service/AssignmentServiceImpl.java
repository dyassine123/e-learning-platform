package com.elearning.elearning_platform.assignment.service;

import com.elearning.elearning_platform.assignment.domain.Assignment;
import com.elearning.elearning_platform.assignment.domain.AssignmentStatus;
import com.elearning.elearning_platform.assignment.domain.AssignmentSubmission;
import com.elearning.elearning_platform.assignment.dto.*;
import com.elearning.elearning_platform.assignment.repo.AssignmentRepository;
import com.elearning.elearning_platform.assignment.repo.AssignmentSubmissionRepository;
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
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AssignmentServiceImpl(
        AssignmentRepository assignmentRepository,
        AssignmentSubmissionRepository submissionRepository,
        CourseRepository courseRepository,
        UserRepository userRepository,
        EnrollmentRepository enrollmentRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public AssignmentResponse create(Long actorId, String actorRole, AssignmentCreateRequest request) {
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new NotFoundException("Course not found."));

        assertTeacherOrAdmin(actorId, actorRole, course);

        var creator = userRepository.findById(actorId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        Assignment assignment = new Assignment(
            course,
            creator,
            request.title(),
            request.description(),
            request.attachmentUrl(),
            request.dueDate()
        );

        return toResponse(assignmentRepository.save(assignment));
    }

    @Override
    public AssignmentResponse publish(Long actorId, String actorRole, Long assignmentId) {
        Assignment assignment = getAssignment(assignmentId);
        assertTeacherOrAdmin(actorId, actorRole, assignment.getCourse());
        assignment.publish();
        return toResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> listForCourse(Long actorId, String actorRole, Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));

        boolean admin = Role.ADMIN.name().equals(actorRole);
        boolean owner = course.getOwner().getId().equals(actorId);

        List<Assignment> list = (admin || owner)
            ? assignmentRepository.findByCourse_Id(courseId)
            : assignmentRepository.findByCourse_IdAndStatus(courseId, AssignmentStatus.PUBLISHED);

        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public AssignmentSubmissionResponse submit(Long studentId, String actorRole, Long assignmentId, AssignmentSubmissionRequest request) {
        var student = userRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        if (student.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only STUDENT can submit assignments.");
        }

        Assignment assignment = getAssignment(assignmentId);

        if (assignment.getStatus() != AssignmentStatus.PUBLISHED) {
            throw new ForbiddenException("Assignment is not published.");
        }

        Long courseId = assignment.getCourse().getId();
        if (!enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId)) {
            throw new ForbiddenException("You must be enrolled in the course.");
        }

        if (submissionRepository.findByAssignment_IdAndStudent_Id(assignmentId, studentId).isPresent()) {
            throw new ConflictException("You already submitted this assignment.");
        }

        AssignmentSubmission submission = new AssignmentSubmission(
            assignment,
            student,
            request.fileUrl(),
            request.textSubmission()
        );

        return toSubmissionResponse(submissionRepository.save(submission));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmissionResponse> listSubmissions(Long actorId, String actorRole, Long assignmentId) {
        Assignment assignment = getAssignment(assignmentId);
        assertTeacherOrAdmin(actorId, actorRole, assignment.getCourse());

        return submissionRepository.findByAssignment_Id(assignmentId)
            .stream()
            .map(this::toSubmissionResponse)
            .toList();
    }

    @Override
    public AssignmentSubmissionResponse grade(Long actorId, String actorRole, Long submissionId, GradeSubmissionRequest request) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new NotFoundException("Submission not found."));

        assertTeacherOrAdmin(actorId, actorRole, submission.getAssignment().getCourse());

        submission.grade(request.grade(), request.teacherComment());
        return toSubmissionResponse(submission);
    }

    private Assignment getAssignment(Long id) {
        return assignmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Assignment not found."));
    }

    private void assertTeacherOrAdmin(Long actorId, String actorRole, Course course) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        if (Role.TEACHER.name().equals(actorRole) && course.getOwner().getId().equals(actorId)) return;
        throw new ForbiddenException("Only ADMIN or course owner TEACHER can manage assignments.");
    }

    private AssignmentResponse toResponse(Assignment a) {
        return new AssignmentResponse(
            a.getId(),
            a.getCourse().getId(),
            a.getCreatedBy().getId(),
            a.getTitle(),
            a.getDescription(),
            a.getAttachmentUrl(),
            a.getDueDate(),
            a.getStatus(),
            a.getCreatedAt(),
            a.getUpdatedAt()
        );
    }

    private AssignmentSubmissionResponse toSubmissionResponse(AssignmentSubmission s) {
        return new AssignmentSubmissionResponse(
            s.getId(),
            s.getAssignment().getId(),
            s.getStudent().getId(),
            s.getStudent().getFullName(),
            s.getFileUrl(),
            s.getTextSubmission(),
            s.getGrade(),
            s.getTeacherComment(),
            s.getSubmittedAt(),
            s.getGradedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmissionResponse> listMySubmissions(Long studentId, Long courseId) {
        List<AssignmentSubmission> submissions = submissionRepository.findByStudent_Id(studentId);
        
        return submissions.stream()
            .filter(sub -> courseId == null || sub.getAssignment().getCourse().getId().equals(courseId))
            .map(this::toSubmissionResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmissionResponse> listPendingSubmissions(Long actorId, String actorRole, Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found."));
            
        assertTeacherOrAdmin(actorId, actorRole, course);

        // Here we define "pending" as not having a grade yet (getGrade() == null or getGradedAt() == null)
        List<Assignment> courseAssignments = assignmentRepository.findByCourse_Id(courseId);
        
        return courseAssignments.stream()
            .flatMap(a -> submissionRepository.findByAssignment_Id(a.getId()).stream())
            .filter(sub -> sub.getGradedAt() == null) // Pending grading
            .map(this::toSubmissionResponse)
            .toList();
    }
}