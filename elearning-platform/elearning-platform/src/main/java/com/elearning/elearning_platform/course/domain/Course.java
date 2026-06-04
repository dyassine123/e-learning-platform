package com.elearning.elearning_platform.course.domain;

import com.elearning.elearning_platform.category.domain.Category;
import com.elearning.elearning_platform.user.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_courses_status", columnList = "status"),
    @Index(name = "idx_courses_owner", columnList = "owner_id"),
    @Index(name = "idx_courses_category", columnList = "category_id")
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_owner"))
    private User owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_category"))
    private Category category;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    // Business attrs
    @Column(nullable = false)
    private boolean free = true;

    @Column(precision = 10, scale = 2) // ex: 99999999.99
    private BigDecimal price;

    @Column(length = 10)
    private String language; // "EN", "FR", "AR"...

    @Column(length = 30)
    private String level; // "BEGINNER", "INTERMEDIATE", "ADVANCED"

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(length = 500)
    private String previewVideoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private Instant publishedAt;

    protected Course() {}

    public Course(
        User owner,
        Category category,
        String title,
        String description,
        boolean free,
        BigDecimal price,
        String language,
        String level,
        String thumbnailUrl,
        String previewVideoUrl
    ) {
        this.owner = owner;
        this.category = category;
        this.title = title;
        this.description = description;
        this.free = free;
        this.price = price;
        this.language = language;
        this.level = level;
        this.thumbnailUrl = thumbnailUrl;
        this.previewVideoUrl = previewVideoUrl;
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

    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public Category getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isFree() { return free; }
    public BigDecimal getPrice() { return price; }
    public String getLanguage() { return language; }
    public String getLevel() { return level; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getPreviewVideoUrl() { return previewVideoUrl; }
    public CourseStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getPublishedAt() { return publishedAt; }

    public void updateContentAndBusiness(
        Category category,
        String title,
        String description,
        boolean free,
        BigDecimal price,
        String language,
        String level,
        String thumbnailUrl,
        String previewVideoUrl
    ) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.free = free;
        this.price = price;
        this.language = language;
        this.level = level;
        this.thumbnailUrl = thumbnailUrl;
        this.previewVideoUrl = previewVideoUrl;
    }

    public void publish() {
        if (status == CourseStatus.ARCHIVED) throw new IllegalStateException("Archived course can't be published.");
        status = CourseStatus.PUBLISHED;
        publishedAt = Instant.now();
    }

    public void unpublishToDraft() {
        if (status == CourseStatus.ARCHIVED) throw new IllegalStateException("Archived course can't be changed.");
        status = CourseStatus.DRAFT;
        publishedAt = null;
    }

    public void archive() {
        status = CourseStatus.ARCHIVED;
    }

    public void submitForApproval() {
        if (status != CourseStatus.DRAFT) throw new IllegalStateException("Only DRAFT courses can be submitted.");
        status = CourseStatus.PENDING_APPROVAL;
    }

    public void rejectToDraft() {
        if (status != CourseStatus.PENDING_APPROVAL) throw new IllegalStateException("Only PENDING courses can be rejected.");
        status = CourseStatus.DRAFT;
    }
}