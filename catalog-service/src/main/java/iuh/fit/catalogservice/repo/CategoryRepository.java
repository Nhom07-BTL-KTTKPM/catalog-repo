package iuh.fit.catalogservice.repo;

import iuh.fit.catalogservice.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Category entity
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    /**
     * Tìm danh mục dựa theo đường dẫn URL (slug).
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Lấy toàn bộ danh mục đang hoạt động.
     * Dữ liệu được sắp xếp dựa trên thứ tự hiển thị (displayOrder) được cấu hình bởi Admin.
     */
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Lấy các Danh mục Gốc (Root Categories).
     * Đây là những danh mục cấp 1, không có danh mục cha (parentId = null).
     */
    List<Category> findByParentIdIsNullAndIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Lấy danh sách các danh mục con thuộc về một danh mục cha cụ thể.
     * @param parentId ID của danh mục cha.
     */
    List<Category> findByParentIdAndIsActiveTrueOrderByDisplayOrderAsc(UUID parentId);

    /**
     * Lấy toàn bộ danh mục dưới dạng phân trang (Dành cho Admin).
     */
    Page<Category> findAll(Pageable pageable);

    /**
     * Kiểm tra tính duy nhất của slug trước khi lưu dữ liệu.
     */
    boolean existsBySlug(String slug);

    /**
     * Tìm kiếm danh mục theo tên, chỉ lấy các danh mục đang mở.
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND c.isActive = true")
    List<Category> searchByName(@Param("keyword") String keyword);

    /**
     * Lấy danh sách rút gọn (id, name, slug, imageUrl) của tất cả danh mục đang hoạt động.
     * Sắp xếp theo displayOrder để giữ đúng thứ tự hiển thị do Admin cấu hình.
     * Dùng cho menu, sidebar, bộ lọc trên giao diện.
     */
    @Query("SELECT new iuh.fit.catalogservice.dto.response.CategorySummaryResponse(" +
            "c.id, c.name, c.slug, c.imageUrl) " +
            "FROM Category c WHERE c.isActive = true ORDER BY c.displayOrder ASC")
    List<iuh.fit.catalogservice.dto.response.CategorySummaryResponse> findAllActiveSummaries();

    /**
     * Lấy danh sách rút gọn của các danh mục gốc (parentId = null) đang hoạt động.
     * Phù hợp cho thanh menu chính ở header trang chủ.
     */
    @Query("SELECT new iuh.fit.catalogservice.dto.response.CategorySummaryResponse(" +
            "c.id, c.name, c.slug, c.imageUrl) " +
            "FROM Category c WHERE c.isActive = true AND c.parentId IS NULL " +
            "ORDER BY c.displayOrder ASC")
    List<iuh.fit.catalogservice.dto.response.CategorySummaryResponse> findRootActiveSummaries();
}

