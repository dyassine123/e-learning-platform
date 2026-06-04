package com.elearning.elearning_platform.course.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

import com.elearning.elearning_platform.user.domain.User;

@Entity
@Table(
    name = "lesson_progress",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_progress_student_lesson", columnNames = {"student_id", "lesson_id"})
    },
    indexes = {
        @Index(name = "idx_progress_student", columnList = "student_id"),
        @Index(name = "idx_progress_lesson", columnList = "lesson_id")
    }
)
public class LessonProgress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_id", nullable = false)
  private Lesson lesson;

  @Column(name = "completed", nullable = false)
  private boolean completed;

  @Column(name = "progress_percent", nullable = false)
  private int progressPercent; // 0..100

  @Column(name = "last_seen_at", nullable = false)
  private Instant lastSeenAt;

  protected LessonProgress() {}

  private LessonProgress(User student, Lesson lesson) {
    this.student = Objects.requireNonNull(student);
    this.lesson = Objects.requireNonNull(lesson);
    this.completed = false;
    this.progressPercent = 0;
    this.lastSeenAt = Instant.now();
  }

  public static LessonProgress start(User student, Lesson lesson) {
    return new LessonProgress(student, lesson);
  }

  public void markProgress(int percent) {
    if (percent < 0 || percent > 100) throw new IllegalArgumentException("progress must be 0..100");
    this.progressPercent = percent;
    this.completed = percent == 100;
    this.lastSeenAt = Instant.now();
  }

  public Long getId() { return id; }
  public User getStudent() { return student; }
  public Lesson getLesson() { return lesson; }
  public boolean isCompleted() { return completed; }
  public int getProgressPercent() { return progressPercent; }
  public Instant getLastSeenAt() { return lastSeenAt; }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LessonProgress other)) return false;
    return id != null && id.equals(other.id);
  }
  @Override public int hashCode() { return 31; }
}