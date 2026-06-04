package com.elearning.elearning_platform.assessment.domain;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.user.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "quizzes", indexes = {
    @Index(name = "idx_quiz_course", columnList = "course_id"),
    @Index(name = "idx_quiz_status", columnList = "status")
})
public class Quiz {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, length = 160)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuizStatus status = QuizStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Quiz() {}

    public Quiz(Course course, User createdBy, String title) {
        this.course = course;
        this.createdBy = createdBy;
        this.title = title.trim();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void publish() {
        if (status == QuizStatus.ARCHIVED) throw new IllegalStateException("Archived quiz cannot be published.");
        status = QuizStatus.PUBLISHED;
    }

    public void archive() { status = QuizStatus.ARCHIVED; }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public User getCreatedBy() { return createdBy; }
    public String getTitle() { return title; }
    public QuizStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}