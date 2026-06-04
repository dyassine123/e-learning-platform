package com.elearning.elearning_platform.assessment.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_questions", indexes = {
    @Index(name = "idx_question_quiz", columnList = "quiz_id")
})
public class QuizQuestion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType type;

    @Column(nullable = false, length = 1200)
    private String questionText;

    @Column(nullable = false)
    private int points = 1;

    // For TRUE_FALSE and SHORT_ANSWER grading
    @Column(length = 800)
    private String correctAnswerText;

    protected QuizQuestion() {}

    public QuizQuestion(Quiz quiz, QuestionType type, String questionText, int points, String correctAnswerText) {
        this.quiz = quiz;
        this.type = type;
        this.questionText = questionText.trim();
        this.points = points;
        this.correctAnswerText = (correctAnswerText == null ? null : correctAnswerText.trim());
    }

    public Long getId() { return id; }
    public Quiz getQuiz() { return quiz; }
    public QuestionType getType() { return type; }
    public String getQuestionText() { return questionText; }
    public int getPoints() { return points; }
    public String getCorrectAnswerText() { return correctAnswerText; }
}