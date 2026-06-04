package com.elearning.elearning_platform.enrollment.domain;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.user.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name = "enrollments",
    uniqueConstraints = @UniqueConstraint(name = "uk_enrollment_student_course", columnNames = {"student_id", "course_id"}),
    indexes = {
        @Index(name = "idx_enroll_student", columnList = "student_id"),
        @Index(name = "idx_enroll_course", columnList = "course_id")
    }
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enroll_student"))
    private User student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enroll_course"))
    private Course course;

    @Column(nullable = false, updatable = false)
    private Instant enrolledAt;

    protected Enrollment() {}

    public Enrollment(User student, Course course) {
        this.student = student;
        this.course = course;
    }

    @PrePersist
    void onCreate() {
        enrolledAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getStudent() { return student; }
    public Course getCourse() { return course; }
    public Instant getEnrolledAt() { return enrolledAt; }
}