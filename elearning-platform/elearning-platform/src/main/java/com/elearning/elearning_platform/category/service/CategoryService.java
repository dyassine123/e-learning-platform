package com.elearning.elearning_platform.category.service;

import com.elearning.elearning_platform.category.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponse create(String actorRole, CategoryCreateRequest req);
    CategoryResponse update(String actorRole, Long id, CategoryUpdateRequest req);
    void delete(String actorRole, Long id);
    Page<CategoryResponse> list(Pageable pageable);
}