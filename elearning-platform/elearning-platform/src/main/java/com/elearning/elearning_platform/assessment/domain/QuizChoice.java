package com.elearning.elearning_platform.assessment.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_choices", indexes = {
    @Index(name = "idx_choice_question", columnList = "question_id")
})
public class QuizChoice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(nullable = false, length = 400)
    private String choiceText;

    @Column(nullable = false)
    private boolean correct;

    protected QuizChoice() {}

    public QuizChoice(QuizQuestion question, String choiceText, boolean correct) {
        this.question = question;
        this.choiceText = choiceText.trim();
        this.correct = correct;
    }

    public Long getId() { return id; }
    public QuizQuestion getQuestion() { return question; }
    public String getChoiceText() { return choiceText; }
    public boolean isCorrect() { return correct; }
}