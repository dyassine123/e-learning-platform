package com.elearning.elearning_platform.announcement.api;

import com.elearning.elearning_platform.announcement.dto.*;
import com.elearning.elearning_platform.announcement.service.AnnouncementService;
import com.elearning.elearning_platform.shared.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final CurrentUser currentUser;

    public AnnouncementController(AnnouncementService announcementService, CurrentUser currentUser) {
        this.announcementService = announcementService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public AnnouncementResponse create(Authentication auth, @Valid @RequestBody AnnouncementCreateRequest request) {
        return announcementService.create(currentUser.userId(auth), currentUser.role(auth), request);
    }

    @GetMapping("/course/{courseId}")
    public List<AnnouncementResponse> listForCourse(Authentication auth, @PathVariable Long courseId) {
        return announcementService.listForCourse(currentUser.userId(auth), currentUser.role(auth), courseId);
    }

    @GetMapping("/me")
    public List<AnnouncementResponse> listMyAnnouncements(Authentication auth) {
        return announcementService.listMyAnnouncements(currentUser.userId(auth));
    }
}