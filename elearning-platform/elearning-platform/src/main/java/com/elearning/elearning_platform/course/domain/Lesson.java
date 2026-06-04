package com.elearning.elearning_platform.course.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
    name = "lessons",
    indexes = {
        @Index(name = "idx_lesson_section", columnList = "section_id"),
        @Index(name = "idx_lesson_section_order", columnList = "section_id,display_order")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_lesson_section_order", columnNames = {"section_id", "display_order"})
    }
)
public class Lesson {

  public enum LessonType { VIDEO, ARTICLE, QUIZ }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "section_id", nullable = false)
  private CourseSection section;

  @Column(name = "title", nullable = false, length = 160)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private LessonType type;

  @Column(name = "display_order", nullable = false)
  private int displayOrder;

  @Column(name = "estimated_minutes")
  private Integer estimatedMinutes;

  @Column(name = "published", nullable = false)
  private boolean published;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected Lesson() {}

  private Lesson(CourseSection section, String title, LessonType type, int displayOrder, Integer estimatedMinutes) {
    this.section = Objects.requireNonNull(section);
    this.title = requireText(title, "title");
    this.type = Objects.requireNonNull(type);
    this.displayOrder = displayOrder;
    this.estimatedMinutes = estimatedMinutes;
    this.published = false;
    this.createdAt = Instant.now();
  }

  public static Lesson create(CourseSection section, String title, LessonType type, int displayOrder, Integer estimatedMinutes) {
    if (displayOrder < 1) throw new IllegalArgumentException("displayOrder must be >= 1");
    if (estimatedMinutes != null && estimatedMinutes <= 0) throw new IllegalArgumentException("estimatedMinutes must be > 0");
    return new Lesson(section, title, type, displayOrder, estimatedMinutes);
  }

  // Small domain behaviors (no setters)
  public void publish() { this.published = true; }
  public void unpublish() { this.published = false; }

  public Long getId() { return id; }
  public CourseSection getSection() { return section; }
  public String getTitle() { return title; }
  public LessonType getType() { return type; }
  public int getDisplayOrder() { return displayOrder; }
  public Integer getEstimatedMinutes() { return estimatedMinutes; }
  public boolean isPublished() { return published; }
  public Instant getCreatedAt() { return createdAt; }

  private static String requireText(String v, String field) {
    if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(field + " is required");
    return v.trim();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Lesson other)) return false;
    return id != null && id.equals(other.id);
  }
  @Override public int hashCode() { return 31; }
}