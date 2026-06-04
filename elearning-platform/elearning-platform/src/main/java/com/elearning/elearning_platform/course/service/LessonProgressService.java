package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.dto.LessonProgressResponse;

import java.util.List;

public interface LessonProgressService {
    LessonProgressResponse updateProgress(Long studentId, Long lessonId, int progressPercent);
    List<LessonProgressResponse> listMyProgress(Long studentId);
}