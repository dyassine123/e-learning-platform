package com.elearning.elearning_platform.course.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
    name = "lesson_materials",
    indexes = {
        @Index(name = "idx_material_lesson", columnList = "lesson_id")
    }
)
public class LessonMaterial {

  public enum MaterialType { VIDEO_URL, PDF_URL, EXTERNAL_LINK, TEXT }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_id", nullable = false)
  private Lesson lesson;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 30)
  private MaterialType type;

  @Column(name = "title", length = 160)
  private String title;

  @Column(name = "content", nullable = false, length = 2000)
  private String content;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected LessonMaterial() {}

  private LessonMaterial(Lesson lesson, MaterialType type, String title, String content) {
    this.lesson = Objects.requireNonNull(lesson);
    this.type = Objects.requireNonNull(type);
    this.title = title == null ? null : title.trim();
    this.content = requireText(content, "content");
    this.createdAt = Instant.now();
  }

  public static LessonMaterial create(Lesson lesson, MaterialType type, String title, String content) {
    return new LessonMaterial(lesson, type, title, content);
  }

  public Long getId() { return id; }
  public Lesson getLesson() { return lesson; }
  public MaterialType getType() { return type; }
  public String getTitle() { return title; }
  public String getContent() { return content; }
  public Instant getCreatedAt() { return createdAt; }

  private static String requireText(String v, String field) {
    if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(field + " is required");
    return v.trim();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LessonMaterial other)) return false;
    return id != null && id.equals(other.id);
  }
  @Override public int hashCode() { return 31; }
}