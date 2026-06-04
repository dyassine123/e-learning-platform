package com.elearning.elearning_platform.assessment.domain;

import com.elearning.elearning_platform.user.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name = "quiz_submissions",
    indexes = {
        @Index(name = "idx_submission_quiz", columnList = "quiz_id"),
        @Index(name = "idx_submission_student", columnList = "student_id")
    }
)
public class QuizSubmission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(nullable = false)
    private int score = 0;

    protected QuizSubmission() {}

    public QuizSubmission(Quiz quiz, User student) {
        this.quiz = quiz;
        this.student = student;
    }

    @PrePersist
    void onCreate() {
        submittedAt = Instant.now();
    }

    public void setScore(int score) { this.score = score; }

    public Long getId() { return id; }
    public Quiz getQuiz() { return quiz; }
    public User getStudent() { return student; }
    public Instant getSubmittedAt() { return submittedAt; }
    public int getScore() { return score; }
}