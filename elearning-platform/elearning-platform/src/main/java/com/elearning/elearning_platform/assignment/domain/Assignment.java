package com.elearning.elearning_platform.assignment.domain;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.user.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "assignments", indexes = {
    @Index(name = "idx_assignment_course", columnList = "course_id"),
    @Index(name = "idx_assignment_status", columnList = "status")
})
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(length = 500)
    private String attachmentUrl;

    private Instant dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status = AssignmentStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Assignment() {}

    public Assignment(Course course, User createdBy, String title, String description, String attachmentUrl, Instant dueDate) {
        this.course = course;
        this.createdBy = createdBy;
        this.title = title.trim();
        this.description = description.trim();
        this.attachmentUrl = attachmentUrl;
        this.dueDate = dueDate;
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

    public void publish() { this.status = AssignmentStatus.PUBLISHED; }
    public void close() { this.status = AssignmentStatus.CLOSED; }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public User getCreatedBy() { return createdBy; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public Instant getDueDate() { return dueDate; }
    public AssignmentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}