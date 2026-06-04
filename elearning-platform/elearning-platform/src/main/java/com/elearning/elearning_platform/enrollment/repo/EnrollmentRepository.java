package com.elearning.elearning_platform.enrollment.repo;

import com.elearning.elearning_platform.enrollment.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    Optional<Enrollment> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    Page<Enrollment> findByStudent_Id(Long studentId, Pageable pageable);

    List<Enrollment> findByCourse_Id(Long courseId);
    Page<Enrollment> findByCourse_Id(Long courseId, Pageable pageable);
}