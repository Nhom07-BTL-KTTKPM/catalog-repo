package iuh.fit.catalogservice.controller;

import iuh.fit.catalogservice.dto.request.CategoryRequest;
import iuh.fit.catalogservice.dto.response.CategoryResponse;
import iuh.fit.catalogservice.dto.response.CategorySummaryResponse;
import iuh.fit.catalogservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Category management
 */
@RestController
@RequestMapping("/api/v1/catalog/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        CategoryResponse response = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CategoryResponse> response = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CategoryResponse>> getActiveCategories() {
        List<CategoryResponse> response = categoryService.getActiveCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/root")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        List<CategoryResponse> response = categoryService.getRootCategories();
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách rút gọn của toàn bộ danh mục đang hoạt động.
     * Trả về (id, name, slug, imageUrl) - dùng cho menu, sidebar, filter UI.
     */
    @GetMapping("/summary")
    public ResponseEntity<List<CategorySummaryResponse>> getActiveCategorySummaries() {
        List<CategorySummaryResponse> response = categoryService.getActiveCategorySummaries();
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách rút gọn của các danh mục gốc đang hoạt động (parentId = null).
     * Dùng cho menu chính/khung danh mục ở trang chủ.
     */
    @GetMapping("/summary/root")
    public ResponseEntity<List<CategorySummaryResponse>> getRootCategorySummaries() {
        List<CategorySummaryResponse> response = categoryService.getRootCategorySummaries();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<CategoryResponse>> getChildCategories(@PathVariable UUID parentId) {
        List<CategoryResponse> response = categoryService.getChildCategories(parentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> searchCategories(@RequestParam String keyword) {
        List<CategoryResponse> response = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(response);
    }
}

