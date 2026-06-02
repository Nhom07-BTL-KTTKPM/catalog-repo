package iuh.fit.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ProductImage entity - manages product images with ordering and primary flag
 */
@Entity
@Table(name = "product_images", indexes = {
        @Index(name = "idx_product_image_product_id", columnList = "product_id"),
        @Index(name = "idx_product_image_is_primary", columnList = "is_primary")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    /**
     * Liên kết với sản phẩm chủ quản (Many-to-One).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    /**
     * Đường dẫn tuyệt đối của hình ảnh (Ví dụ: https://res.cloudinary.com/...).
     */
    @Column(nullable = false, length = 500)
    private String url;
    /**
     * Mã định danh trên dịch vụ Cloudinary
     * Cần thiết để thực hiện thao tác xóa hoặc cập nhật ảnh gốc trên Cloud.
     */
    @Column(name = "public_id", length = 255)
    private String publicId;
    /**
     * Văn bản thay thế cho hình ảnh.
     * Quan trọng cho SEO hình ảnh và khả năng truy cập (accessibility) cho người khiếm thị.
     */
    @Column(name = "alt_text", length = 500)
    private String altText;
    /**
     * Thứ tự hiển thị trong bộ sưu tập (Slider/Gallery).
     * Số nhỏ hơn sẽ được ưu tiên hiển thị trước.
     * Bắt đầu từ 0
     */
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
    /**
     * Đánh dấu ảnh này là ảnh đại diện chính của sản phẩm.
     */
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;
    /**
     * Thời điểm tải ảnh lên hệ thống.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

