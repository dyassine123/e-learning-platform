package com.elearning.elearning_platform.assessment.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_answers", indexes = {
    @Index(name = "idx_answer_submission", columnList = "submission_id"),
    @Index(name = "idx_answer_question", columnList = "question_id")
})
public class QuizAnswer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private QuizSubmission submission;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    // For MCQ: store selected choice id (nullable for others)
    private Long selectedChoiceId;

    // For text answers
    @Column(length = 1200)
    private String answerText;

    @Column(nullable = false)
    private boolean correct;

    protected QuizAnswer() {}

    public QuizAnswer(QuizSubmission submission, QuizQuestion question, Long selectedChoiceId, String answerText, boolean correct) {
        this.submission = submission;
        this.question = question;
        this.selectedChoiceId = selectedChoiceId;
        this.answerText = answerText == null ? null : answerText.trim();
        this.correct = correct;
    }

    public Long getId() { return id; }
    public QuizSubmission getSubmission() { return submission; }
    public QuizQuestion getQuestion() { return question; }
    public Long getSelectedChoiceId() { return selectedChoiceId; }
    public String getAnswerText() { return answerText; }
    public boolean isCorrect() { return correct; }
}