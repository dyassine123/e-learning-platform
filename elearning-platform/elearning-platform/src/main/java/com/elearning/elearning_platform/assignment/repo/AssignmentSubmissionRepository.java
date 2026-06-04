package com.elearning.elearning_platform.assignment.repo;

import com.elearning.elearning_platform.assignment.domain.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    Optional<AssignmentSubmission> findByAssignment_IdAndStudent_Id(Long assignmentId, Long studentId);
    List<AssignmentSubmission> findByAssignment_Id(Long assignmentId);
    List<AssignmentSubmission> findByStudent_Id(Long studentId);
}