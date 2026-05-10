package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.dto.request.CategoryRequest;
import iuh.fit.catalogservice.dto.response.CategoryResponse;
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
     * Delete category
     */
    void deleteCategory(UUID id);

    /**
     * Search categories by name
     */
    List<CategoryResponse> searchCategories(String keyword);
}

