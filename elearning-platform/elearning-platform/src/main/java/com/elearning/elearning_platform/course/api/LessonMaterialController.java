package com.elearning.elearning_platform.course.api;

import com.elearning.elearning_platform.course.dto.LessonMaterialResponse;
import com.elearning.elearning_platform.course.service.LessonMaterialService;
import com.elearning.elearning_platform.shared.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/lesson-materials")
public class LessonMaterialController {

    private final LessonMaterialService materialService;
    private final CurrentUser currentUser;

    public LessonMaterialController(LessonMaterialService materialService, CurrentUser currentUser) {
        this.materialService = materialService;
        this.currentUser = currentUser;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public LessonMaterialResponse create(
            Authentication auth,
            @RequestParam("lessonId") Long lessonId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam("file") MultipartFile file) {
        return materialService.create(currentUser.userId(auth), currentUser.role(auth), lessonId, title, file);
    }

    @GetMapping("/lesson/{lessonId}")
    public List<LessonMaterialResponse> listByLesson(Authentication auth, @PathVariable Long lessonId) {
        return materialService.listByLesson(currentUser.userId(auth), currentUser.role(auth), lessonId);
    }

    @DeleteMapping("/{materialId}")
    public void delete(Authentication auth, @PathVariable Long materialId) {
        materialService.delete(currentUser.userId(auth), currentUser.role(auth), materialId);
    }
}