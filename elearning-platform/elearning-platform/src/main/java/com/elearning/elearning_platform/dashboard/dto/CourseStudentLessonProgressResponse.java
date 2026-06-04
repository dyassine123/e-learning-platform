package com.elearning.elearning_platform.dashboard.dto;

import java.util.List;

public record CourseStudentLessonProgressResponse(
    Long courseId,
    String courseTitle,
    int totalLessons,
    List<StudentProgressRow> students
) {
    public record StudentProgressRow(
        Long studentId,
        String fullName,
        String email,
        int overallProgressPercent,
        int completedLessons,
        int totalLessons,
        List<LessonProgressRow> lessons
    ) {}

    public record LessonProgressRow(
        Long lessonId,
        String lessonTitle,
        int displayOrder,
        int progressPercent,
        boolean completed
    ) {}
}
