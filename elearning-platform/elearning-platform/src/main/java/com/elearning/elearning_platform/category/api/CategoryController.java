package com.elearning.elearning_platform.category.api;

import com.elearning.elearning_platform.category.dto.*;
import com.elearning.elearning_platform.category.service.CategoryService;
import com.elearning.elearning_platform.shared.security.CurrentUser;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;
    private final CurrentUser currentUser;

    public CategoryController(CategoryService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping
    public Page<CategoryResponse> list(Pageable pageable) {
        return service.list(pageable);
    }

    @PostMapping
    public CategoryResponse create(Authentication auth, @Valid @RequestBody CategoryCreateRequest req) {
        return service.create(currentUser.role(auth), req);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(Authentication auth, @PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest req) {
        return service.update(currentUser.role(auth), id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(Authentication auth, @PathVariable Long id) {
        service.delete(currentUser.role(auth), id);
    }
}