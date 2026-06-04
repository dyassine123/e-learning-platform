package com.elearning.elearning_platform.review.domain;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.user.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name = "course_reviews",
    uniqueConstraints = @UniqueConstraint(name = "uk_review_student_course", columnNames = {"student_id","course_id"}),
    indexes = {
        @Index(name = "idx_review_course", columnList = "course_id"),
        @Index(name = "idx_review_student", columnList = "student_id")
    }
)
public class CourseReview {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private int rating; // 1..5

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected CourseReview() {}

    public CourseReview(Course course, User student, int rating, String comment) {
        this.course = course;
        this.student = student;
        this.rating = rating;
        this.comment = comment == null ? null : comment.trim();
    }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public void update(int rating, String comment) {
        this.rating = rating;
        this.comment = comment == null ? null : comment.trim();
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public User getStudent() { return student; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
}