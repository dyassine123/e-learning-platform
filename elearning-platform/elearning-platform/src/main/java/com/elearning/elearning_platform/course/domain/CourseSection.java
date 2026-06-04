package com.elearning.elearning_platform.course.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
    name = "course_sections",
    indexes = {
        @Index(name = "idx_section_course", columnList = "course_id"),
        @Index(name = "idx_section_course_order", columnList = "course_id,display_order")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_section_course_order", columnNames = {"course_id", "display_order"})
    }
)
public class CourseSection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @Column(name = "title", nullable = false, length = 140)
  private String title;

  @Column(name = "description", length = 600)
  private String description;

  @Column(name = "display_order", nullable = false)
  private int displayOrder;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected CourseSection() {}

  private CourseSection(Course course, String title, String description, int displayOrder) {
    this.course = Objects.requireNonNull(course);
    this.title = requireText(title, "title");
    this.description = description;
    this.displayOrder = displayOrder;
    this.createdAt = Instant.now();
  }

  public static CourseSection create(Course course, String title, String description, int displayOrder) {
    if (displayOrder < 1) throw new IllegalArgumentException("displayOrder must be >= 1");
    return new CourseSection(course, title, description, displayOrder);
  }

  public Long getId() { return id; }
  public Course getCourse() { return course; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public int getDisplayOrder() { return displayOrder; }
  public Instant getCreatedAt() { return createdAt; }

  private static String requireText(String v, String field) {
    if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(field + " is required");
    return v.trim();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CourseSection other)) return false;
    return id != null && id.equals(other.id);
  }
  @Override public int hashCode() { return 31; }
}