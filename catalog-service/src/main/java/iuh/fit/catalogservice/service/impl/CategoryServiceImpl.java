package iuh.fit.catalogservice.service.impl;

import iuh.fit.catalogservice.dto.request.CategoryRequest;
import iuh.fit.catalogservice.dto.request.CategoryStatusRequest;
import iuh.fit.catalogservice.dto.response.CategoryResponse;
import iuh.fit.catalogservice.dto.response.CategorySummaryResponse;
import iuh.fit.catalogservice.entity.Category;
import iuh.fit.catalogservice.repo.CategoryRepository;
import iuh.fit.catalogservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of CategoryService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating new category with slug: {}", request.getSlug());
        
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Category with slug '" + request.getSlug() + "' already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .parentId(request.getParentId())
                .isActive(request.getIsActive())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Created category with ID: {}", savedCategory.getId());
        
        return mapToResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        log.info("Updating category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        // Check slug uniqueness if changed
        if (!category.getSlug().equals(request.getSlug()) && 
            categoryRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Category with slug '" + request.getSlug() + "' already exists");
        }

        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setParentId(request.getParentId());
        category.setIsActive(request.getIsActive());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Updated category with ID: {}", updatedCategory.getId());
        
        return mapToResponse(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        log.debug("Fetching category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
        
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        log.debug("Fetching category with slug: {}", slug);
        
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with slug: " + slug));
        
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        log.debug("Fetching all categories with pagination");
        
        return categoryRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        log.debug("Fetching all active categories");
        
        return categoryRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        log.debug("Fetching root categories");
        
        return categoryRepository.findByParentIdIsNullAndIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getChildCategories(UUID parentId) {
        log.debug("Fetching child categories for parent ID: {}", parentId);
        
        return categoryRepository.findByParentIdAndIsActiveTrueOrderByNameAsc(parentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategoryIsActive(UUID id, CategoryStatusRequest request) {
        log.info("Updating category isActive status with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        category.setIsActive(request.getIsActive());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Updated category isActive status with ID: {}", updatedCategory.getId());

        return mapToResponse(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategories(String keyword) {
        log.debug("Searching categories with keyword: {}", keyword);

        return categoryRepository.searchByName(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getActiveCategorySummaries() {
        log.debug("Fetching active category summaries");
        return categoryRepository.findAllActiveSummaries();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getRootCategorySummaries() {
        log.debug("Fetching root active category summaries");
        return categoryRepository.findRootActiveSummaries();
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParentId())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

