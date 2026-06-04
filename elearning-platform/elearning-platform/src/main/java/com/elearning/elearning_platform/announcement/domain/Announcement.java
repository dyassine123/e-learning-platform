package com.elearning.elearning_platform.announcement.domain;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.user.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "announcements", indexes = {
    @Index(name = "idx_announcement_course", columnList = "course_id")
})
public class Announcement {

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
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Announcement() {}

    public Announcement(Course course, User createdBy, String title, String content) {
        this.course = course;
        this.createdBy = createdBy;
        this.title = title.trim();
        this.content = content.trim();
    }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public User getCreatedBy() { return createdBy; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
}