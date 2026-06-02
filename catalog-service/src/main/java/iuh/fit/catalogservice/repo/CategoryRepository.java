package iuh.fit.catalogservice.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import iuh.fit.catalogservice.entity.Category;

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
        * Dữ liệu được sắp xếp theo tên danh mục để đảm bảo thứ tự ổn định.
     */
    List<Category> findByIsActiveTrueOrderByNameAsc();

    /**
     * Lấy các Danh mục Gốc (Root Categories).
     * Đây là những danh mục cấp 1, không có danh mục cha (parentId = null).
     */
        List<Category> findByParentIdIsNullAndIsActiveTrueOrderByNameAsc();

    /**
     * Lấy danh sách các danh mục con thuộc về một danh mục cha cụ thể.
     * @param parentId ID của danh mục cha.
     */
        List<Category> findByParentIdAndIsActiveTrueOrderByNameAsc(UUID parentId);


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
     * Sắp xếp theo tên để giữ thứ tự ổn định.
     * Dùng cho menu, sidebar, bộ lọc trên giao diện.
     */
    @Query("SELECT new iuh.fit.catalogservice.dto.response.CategorySummaryResponse(" +
            "c.id, c.name, c.slug, c.imageUrl) " +
            "FROM Category c WHERE c.isActive = true ORDER BY c.name ASC")
    List<iuh.fit.catalogservice.dto.response.CategorySummaryResponse> findAllActiveSummaries();

    /**
     * Lấy danh sách rút gọn của các danh mục gốc (parentId = null) đang hoạt động.
     * Phù hợp cho thanh menu chính ở header trang chủ.
     */
    @Query("SELECT new iuh.fit.catalogservice.dto.response.CategorySummaryResponse(" +
            "c.id, c.name, c.slug, c.imageUrl) " +
            "FROM Category c WHERE c.isActive = true AND c.parentId IS NULL " +
            "ORDER BY c.name ASC")
    List<iuh.fit.catalogservice.dto.response.CategorySummaryResponse> findRootActiveSummaries();
}

