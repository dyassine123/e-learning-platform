package com.elearning.elearning_platform.announcement.service;

import com.elearning.elearning_platform.announcement.dto.*;

import java.util.List;

public interface AnnouncementService {
    AnnouncementResponse create(Long actorId, String actorRole, AnnouncementCreateRequest request);
    List<AnnouncementResponse> listForCourse(Long actorId, String actorRole, Long courseId);
    
    // Missing methods added
    List<AnnouncementResponse> listMyAnnouncements(Long studentId);
}