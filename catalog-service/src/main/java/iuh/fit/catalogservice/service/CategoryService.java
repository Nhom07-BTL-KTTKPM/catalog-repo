package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.dto.request.CategoryRequest;
import iuh.fit.catalogservice.dto.request.CategoryStatusRequest;
import iuh.fit.catalogservice.dto.response.CategoryResponse;
import iuh.fit.catalogservice.dto.response.CategorySummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Category operations
 */
public interface CategoryService {

    /**
     * Create a new category
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Update an existing category
     */
    CategoryResponse updateCategory(UUID id, CategoryRequest request);

    /**
     * Get category by ID
     */
    CategoryResponse getCategoryById(UUID id);

    /**
     * Get category by slug
     */
    CategoryResponse getCategoryBySlug(String slug);

    /**
     * Get all categories with pagination
     */
    Page<CategoryResponse> getAllCategories(Pageable pageable);

    /**
     * Get all active categories
     */
    List<CategoryResponse> getActiveCategories();

    /**
     * Get root categories (no parent)
     */
    List<CategoryResponse> getRootCategories();

    /**
     * Get child categories of a parent
     */
    List<CategoryResponse> getChildCategories(UUID parentId);

    /**
     * Update category active status
     */
    CategoryResponse updateCategoryIsActive(UUID id, CategoryStatusRequest request);

    /**
     * Search categories by name
     */
    List<CategoryResponse> searchCategories(String keyword);

    /**
     * Get lightweight summaries (id, name, slug, imageUrl) of all active categories.
     * Phù hợp cho menu, sidebar, filter UI.
     */
    List<CategorySummaryResponse> getActiveCategorySummaries();

    /**
     * Get lightweight summaries of root active categories (parentId = null).
     * Phù hợp cho menu chính ở header trang chủ.
     */
    List<CategorySummaryResponse> getRootCategorySummaries();
}

