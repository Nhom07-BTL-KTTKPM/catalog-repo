package iuh.fit.catalogservice.repo;

import iuh.fit.catalogservice.entity.Product;
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
 * Repository for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    /**
     * Tìm sản phẩm theo URL SEO.
     */
    Optional<Product> findBySlug(String slug);

    /**
     * Lấy danh sách sản phẩm đang bán, sắp xếp theo thời gian mới nhất (Hàng mới về).
     */
    Page<Product> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Lấy các sản phẩm Nổi bật (isFeatured = true) hiển thị ở Trang chủ.
     * Sắp xếp theo số lượng bán giảm dần (ưu tiên hàng hot).
     */
    Page<Product> findByIsFeaturedTrueAndIsActiveTrueOrderByTotalSoldDesc(Pageable pageable);

    /**
     * Lấy tất cả sản phẩm thuộc một Danh mục cụ thể.
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true")
    Page<Product> findByCategoryIdAndIsActiveTrue(@Param("categoryId") UUID categoryId, Pageable pageable);

    /**
     * Lấy tất cả sản phẩm thuộc một Thương hiệu cụ thể.
     */
    @Query("SELECT p FROM Product p WHERE p.brand.id = :brandId AND p.isActive = true")
    Page<Product> findByBrandIdAndIsActiveTrue(@Param("brandId") UUID brandId, Pageable pageable);

    /**
     * Lọc sản phẩm kết hợp cả Danh mục và Thương hiệu.
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.brand.id = :brandId AND p.isActive = true")
    Page<Product> findByCategoryIdAndBrandIdAndIsActiveTrue(@Param("categoryId") UUID categoryId, @Param("brandId") UUID brandId, Pageable pageable);

    /**
     * Chức năng Thanh tìm kiếm:
     * Tìm từ khóa xuất hiện trong TÊN sản phẩm HOẶC MÔ TẢ sản phẩm.
     */
    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.isActive = true")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy top sản phẩm bán chạy nhất toàn hệ thống (Dựa vào cột totalSold).
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.totalSold DESC")
    Page<Product> findBestSellingProducts(Pageable pageable);

    /**
     * Lấy top sản phẩm được đánh giá cao nhất.
     * Cần điều kiện totalReviews > 0 để loại bỏ các sản phẩm chưa ai mua nhưng điểm mặc định là 0.
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.totalReviews > 0 ORDER BY p.averageRating DESC, p.totalReviews DESC")
    Page<Product> findTopRatedProducts(Pageable pageable);

    /**
     * Gợi ý AI - Lọc theo Loại da (Skin Type).
     * Do dữ liệu lưu dưới dạng chuỗi (Oily,Dry), ta dùng LIKE để tìm.
     * Sử dụng native query để tránh lỗi type mismatch với converter
     */
    @Query(value = "SELECT * FROM products p WHERE " +
            "p.suitable_skin_types LIKE CONCAT('%', :skinType, '%') " +
            "AND p.is_active = true",
            countQuery = "SELECT COUNT(*) FROM products p WHERE " +
            "p.suitable_skin_types LIKE CONCAT('%', :skinType, '%') " +
            "AND p.is_active = true",
            nativeQuery = true)
    Page<Product> findBySkinType(@Param("skinType") String skinType, Pageable pageable);

    /**
     * Gợi ý AI - Lọc theo Vấn đề da (Skin Concern - Mụn, Lão hóa...).
     * Sử dụng native query để tránh lỗi type mismatch với converter
     */
    @Query(value = "SELECT * FROM products p WHERE " +
            "p.skin_concerns LIKE CONCAT('%', :skinConcern, '%') " +
            "AND p.is_active = true",
            countQuery = "SELECT COUNT(*) FROM products p WHERE " +
            "p.skin_concerns LIKE CONCAT('%', :skinConcern, '%') " +
            "AND p.is_active = true",
            nativeQuery = true)
    Page<Product> findBySkinConcern(@Param("skinConcern") String skinConcern, Pageable pageable);

    /**
     * Kiểm tra trùng lặp đường dẫn.
     */
    boolean existsBySlug(String slug);

    /**
     * Bộ lọc giá: Lấy các sản phẩm có mức giá (từ thấp nhất đến cao nhất) nằm trong ngân sách.
     */
    @Query("SELECT p FROM Product p WHERE " +
            "p.minPrice >= :minPrice AND p.maxPrice <= :maxPrice " +
            "AND p.isActive = true")
    Page<Product> findByPriceRange(@Param("minPrice") Double minPrice,
                                   @Param("maxPrice") Double maxPrice,
                                   Pageable pageable);

    /**
     * Đếm tổng số sản phẩm trong một Danh mục.
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true")
    long countByCategoryIdAndIsActiveTrue(@Param("categoryId") UUID categoryId);

    /**
     * Đếm tổng số sản phẩm của một Thương hiệu.
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId AND p.isActive = true")
    long countByBrandIdAndIsActiveTrue(@Param("brandId") UUID brandId);
}