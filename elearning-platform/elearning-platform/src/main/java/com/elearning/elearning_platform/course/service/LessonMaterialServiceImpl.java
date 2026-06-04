package com.elearning.elearning_platform.course.service;

import com.elearning.elearning_platform.course.domain.Course;
import com.elearning.elearning_platform.course.domain.Lesson;
import com.elearning.elearning_platform.course.domain.LessonMaterial;
import com.elearning.elearning_platform.course.domain.LessonMaterial.MaterialType;
import com.elearning.elearning_platform.course.dto.LessonMaterialResponse;
import com.elearning.elearning_platform.course.repo.LessonMaterialRepository;
import com.elearning.elearning_platform.course.repo.LessonRepository;
import com.elearning.elearning_platform.enrollment.repo.EnrollmentRepository;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.shared.storage.FileStorageService;
import com.elearning.elearning_platform.user.domain.Role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class LessonMaterialServiceImpl implements LessonMaterialService {

    private final LessonRepository lessonRepository;
    private final LessonMaterialRepository materialRepository;
    private final FileStorageService fileStorageService;
    private final EnrollmentRepository enrollmentRepository;

    public LessonMaterialServiceImpl(
        LessonRepository lessonRepository,
        LessonMaterialRepository materialRepository,
        FileStorageService fileStorageService,
        EnrollmentRepository enrollmentRepository
    ) {
        this.lessonRepository = lessonRepository;
        this.materialRepository = materialRepository;
        this.fileStorageService = fileStorageService;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public LessonMaterialResponse create(Long actorId, String actorRole, Long lessonId, String title, MultipartFile file) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFoundException("Lesson not found."));

        assertCanManageCourse(actorId, actorRole, lesson.getSection().getCourse());

        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = "/uploads/materials/" + fileName;

        LessonMaterial material = LessonMaterial.create(
            lesson,
            MaterialType.PDF_URL,
            title,
            fileDownloadUri
        );

        return toResponse(materialRepository.save(material));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonMaterialResponse> listByLesson(Long actorId, String actorRole, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new NotFoundException("Lesson not found."));

        Course course = lesson.getSection().getCourse();
        boolean isAdmin = Role.ADMIN.name().equals(actorRole);
        boolean isOwner = course.getOwner().getId().equals(actorId);
        boolean isPublished = course.getStatus().name().equals("PUBLISHED");
        boolean isPublishedLesson = lesson.isPublished();
        boolean isEnrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(actorId, course.getId());

        if (!(isAdmin || isOwner || isEnrolled || (isPublished && isPublishedLesson))) {
            throw new ForbiddenException("You are not allowed to view materials of this lesson.");
        }

        return materialRepository.findByLessonIdOrderByIdAsc(lessonId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public void delete(Long actorId, String actorRole, Long materialId) {
        LessonMaterial material = materialRepository.findById(materialId)
            .orElseThrow(() -> new NotFoundException("Material not found."));

        assertCanManageCourse(actorId, actorRole, material.getLesson().getSection().getCourse());
        materialRepository.delete(material);
    }

    private void assertCanManageCourse(Long actorId, String actorRole, Course course) {
        if (Role.ADMIN.name().equals(actorRole)) return;
        if (Role.TEACHER.name().equals(actorRole) && course.getOwner().getId().equals(actorId)) return;
        throw new ForbiddenException("Only ADMIN or the course owner can manage lesson materials.");
    }

    private LessonMaterialResponse toResponse(LessonMaterial m) {
        return new LessonMaterialResponse(
            m.getId(),
            m.getLesson().getId(),
            m.getType(),
            m.getTitle(),
            m.getContent(),
            m.getCreatedAt()
        );
    }
}