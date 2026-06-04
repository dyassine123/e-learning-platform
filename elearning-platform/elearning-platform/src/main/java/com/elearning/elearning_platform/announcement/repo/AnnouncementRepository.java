package com.elearning.elearning_platform.announcement.repo;

import com.elearning.elearning_platform.announcement.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourse_IdOrderByIdDesc(Long courseId);
    List<Announcement> findByCourse_IdInOrderByIdDesc(List<Long> courseIds);
}