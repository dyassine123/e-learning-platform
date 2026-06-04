package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.dto.LessonMaterialResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface LessonMaterialService {
    LessonMaterialResponse create(Long actorId, String actorRole, Long lessonId, String title, MultipartFile file);
    List<LessonMaterialResponse> listByLesson(Long actorId, String actorRole, Long lessonId);
    void delete(Long actorId, String actorRole, Long materialId);
}