package iuh.fit.catalogservice.repo;

import iuh.fit.catalogservice.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductImage entity
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    /**
     * Lấy toàn bộ bộ sưu tập ảnh của một sản phẩm.
     * Ảnh sẽ được sắp xếp đúng theo thứ tự hiển thị (Slide 1, Slide 2,...)
     * @param productId ID của sản phẩm.
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId = :productId ORDER BY pi.displayOrder ASC")
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(@Param("productId") UUID productId);

    /**
     * Tìm bức ảnh đại diện (Thumbnail chính) của một sản phẩm.
     * Bức ảnh này có isPrimary = true, thường dùng để hiển thị ngoài trang danh sách sản phẩm.
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(@Param("productId") UUID productId);

    /**
     * Xóa sạch toàn bộ hình ảnh liên kết với một sản phẩm.
     * Lưu ý: Thao tác này chỉ xóa bản ghi trong DB, không xóa file gốc trên Cloudinary/S3.
     */
    @Query("DELETE FROM ProductImage pi WHERE pi.product.productId = :productId")
    @Modifying
    void deleteByProductId(@Param("productId") UUID productId);

    /**
     * Đếm xem một sản phẩm đang có tổng cộng bao nhiêu hình ảnh.
     */
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.productId = :productId")
    long countByProductId(@Param("productId") UUID productId);
}