package com.elearning.elearning_platform.assignment.repo;

import com.elearning.elearning_platform.assignment.domain.Assignment;
import com.elearning.elearning_platform.assignment.domain.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourse_Id(Long courseId);
    List<Assignment> findByCourse_IdAndStatus(Long courseId, AssignmentStatus status);
}