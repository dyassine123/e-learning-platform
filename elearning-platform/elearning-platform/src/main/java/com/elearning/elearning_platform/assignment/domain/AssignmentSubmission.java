package com.elearning.elearning_platform.assignment.domain;

import com.elearning.elearning_platform.user.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name = "assignment_submissions",
    uniqueConstraints = @UniqueConstraint(name = "uk_assignment_student", columnNames = {"assignment_id", "student_id"}),
    indexes = {
        @Index(name = "idx_asub_assignment", columnList = "assignment_id"),
        @Index(name = "idx_asub_student", columnList = "student_id")
    }
)
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(length = 500)
    private String fileUrl;

    @Column(length = 3000)
    private String textSubmission;

    private Integer grade;

    @Column(length = 1500)
    private String teacherComment;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt;

    private Instant gradedAt;

    protected AssignmentSubmission() {}

    public AssignmentSubmission(Assignment assignment, User student, String fileUrl, String textSubmission) {
        this.assignment = assignment;
        this.student = student;
        this.fileUrl = fileUrl;
        this.textSubmission = textSubmission;
    }

    @PrePersist
    void onCreate() {
        submittedAt = Instant.now();
    }

    public void grade(int grade, String teacherComment) {
        this.grade = grade;
        this.teacherComment = teacherComment;
        this.gradedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Assignment getAssignment() { return assignment; }
    public User getStudent() { return student; }
    public String getFileUrl() { return fileUrl; }
    public String getTextSubmission() { return textSubmission; }
    public Integer getGrade() { return grade; }
    public String getTeacherComment() { return teacherComment; }
    public Instant getSubmittedAt() { return submittedAt; }
    public Instant getGradedAt() { return gradedAt; }
}