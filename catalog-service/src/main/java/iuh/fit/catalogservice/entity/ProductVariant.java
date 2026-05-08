package iuh.fit.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ProductVariant entity - specific variants of products (e.g., Red Lipstick 5ml, Pink Lipstick 3ml)
 */
@Entity
@Table(name = "product_variants", indexes = {
        @Index(name = "idx_product_variant_product_id", columnList = "product_id"),
        @Index(name = "idx_product_variant_sku", columnList = "sku"),
        @Index(name = "idx_product_variant_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    /**
     * Tham chiếu đến sản phẩm gốc (Many-to-One).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    /**
     * Stock Keeping Unit - Mã định danh duy nhất để quản lý kho vận.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String sku;
    /**
     * Tên mô tả biến thể (Ví dụ: "Màu Đỏ - 5ml", "Tuýp 50g").
     */
    @Column(name = "variant_name", nullable = false, length = 255)
    private String variantName;
    /**
     * Giá bán hiện tại của biến thể.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    /**
     * Giá gốc khi chưa giảm giá.
     * Dùng để hiển thị phần trăm chiết khấu trên giao diện.
     */
    @Column(name = "original_price", precision = 19, scale = 2)
    private BigDecimal originalPrice;
    /**
     * Số lượng hàng hiện có trong kho.
     */
    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;
    /**
     * Tổng số lượng đã bán của riêng biến thể này.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer sold = 0;
    /**
     * Hình ảnh riêng biệt cho biến thể (nếu có).
     * Thường dùng khi sản phẩm có nhiều màu sắc khác nhau.
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    /**
     * Trạng thái kinh doanh của biến thể này.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    /**
     * Thời điểm cập nhật thông tin (giá, kho) gần nhất.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

