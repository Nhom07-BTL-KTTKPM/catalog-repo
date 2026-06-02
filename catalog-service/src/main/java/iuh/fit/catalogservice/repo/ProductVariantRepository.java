package iuh.fit.catalogservice.repo;

import iuh.fit.catalogservice.entity.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductVariant entity
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    /**
     * Tìm biến thể dựa trên mã quản lý kho (SKU - Stock Keeping Unit).
     */
    Optional<ProductVariant> findBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pv FROM ProductVariant pv JOIN FETCH pv.product WHERE pv.id = :variantId")
    Optional<ProductVariant> findByIdForUpdate(@Param("variantId") UUID variantId);

    /**
     * Lấy tất cả biến thể của một sản phẩm (bao gồm cả các mẫu đã ngừng bán).
     * Thường dùng ở trang Admin chỉnh sửa sản phẩm.
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.productId = :productId")
    List<ProductVariant> findByProductId(@Param("productId") UUID productId);

    /**
     * Lấy các biến thể ĐANG ĐƯỢC BÁN của một sản phẩm.
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.productId = :productId AND pv.isActive = true")
    List<ProductVariant> findByProductIdAndIsActiveTrue(@Param("productId") UUID productId);

    /**
     * Đảm bảo mã SKU không bị trùng lặp khi nhập liệu.
     */
    boolean existsBySku(String sku);

    /**
     * Xóa tất cả các biến thể khi sản phẩm gốc bị xóa.
     */
    @Query("DELETE FROM ProductVariant pv WHERE pv.product.productId = :productId")
    @Modifying
    void deleteByProductId(@Param("productId") UUID productId);

    /**
     * Đếm xem sản phẩm này có bao nhiêu biến thể.
     */
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.product.productId = :productId")
    long countByProductId(@Param("productId") UUID productId);

    /**
     * Lấy ra các tùy chọn biến thể CÒN HÀNG (Tồn kho > 0).
     * Dùng để hiển thị các nút chọn dung tích/màu sắc cho khách hàng ở trang chi tiết sản phẩm.
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.productId = :productId AND pv.stockQuantity > 0 AND pv.isActive = true")
    List<ProductVariant> findAvailableVariantsByProductId(@Param("productId") UUID productId);

    /**
     * Tìm biến thể có mức giá RẺ NHẤT của một sản phẩm.
     * Sử dụng LIMIT 1 để tăng tối đa hiệu suất truy vấn.
     * Thường dùng để set giá trị 'minPrice' cho thực thể Product cha.
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.productId = :productId AND pv.isActive = true ORDER BY pv.price ASC LIMIT 1")
    Optional<ProductVariant> findCheapestVariantByProductId(@Param("productId") UUID productId);
}
