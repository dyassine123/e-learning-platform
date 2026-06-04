package com.elearning.elearning_platform.category.service;

import com.elearning.elearning_platform.category.domain.Category;
import com.elearning.elearning_platform.category.dto.*;
import com.elearning.elearning_platform.category.repo.CategoryRepository;
import com.elearning.elearning_platform.shared.error.ConflictException;
import com.elearning.elearning_platform.shared.error.ForbiddenException;
import com.elearning.elearning_platform.user.domain.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;

    public CategoryServiceImpl(CategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public CategoryResponse create(String actorRole, CategoryCreateRequest req) {
        if (!Role.ADMIN.name().equals(actorRole)) throw new ForbiddenException("Only ADMIN can create categories.");

        String name = req.name().trim();
        if (repo.existsByNameIgnoreCase(name)) throw new ConflictException("Category name already exists.");

        Category saved = repo.save(new Category(name, req.description() == null ? null : req.description().trim()));
        return new CategoryResponse(saved.getId(), saved.getName(), saved.getDescription());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription()));
    }

    @Override
    public CategoryResponse update(String actorRole, Long id, CategoryUpdateRequest req) {
        if (!Role.ADMIN.name().equals(actorRole)) throw new ForbiddenException("Only ADMIN can update categories.");

        Category category = repo.findById(id).orElseThrow(() -> new ConflictException("Category not found."));
        String newName = req.name().trim();

        if (!category.getName().equalsIgnoreCase(newName) && repo.existsByNameIgnoreCase(newName)) {
            throw new ConflictException("Category name already exists.");
        }

        category.update(newName, req.description() == null ? null : req.description().trim());

        Category saved = repo.save(category);
        return new CategoryResponse(saved.getId(), saved.getName(), saved.getDescription());
    }

    @Override
    public void delete(String actorRole, Long id) {
        if (!Role.ADMIN.name().equals(actorRole)) throw new ForbiddenException("Only ADMIN can delete categories.");
        if (!repo.existsById(id)) throw new ConflictException("Category not found.");
        repo.deleteById(id);
    }
}