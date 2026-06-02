package iuh.fit.catalogservice.entity;

import iuh.fit.catalogservice.entity.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Product entity - cosmetic products with AI recommendation support
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_slug", columnList = "slug"),
        @Index(name = "idx_product_category_id", columnList = "category_id"),
        @Index(name = "idx_product_brand_id", columnList = "brand_id"),
        @Index(name = "idx_product_is_active", columnList = "is_active"),
        @Index(name = "idx_product_is_featured", columnList = "is_featured")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID productId;

    @Column(nullable = false, length = 500)
    private String name;
    /**
     * Đường dẫn thân thiện SEO, duy nhất trên toàn hệ thống.
     */
    @Column(nullable = false, unique = true, length = 500)
    private String slug;
    /**
     * Mô tả chi tiết sản phẩm .
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    /**
     * Danh sách thành phần hoạt chất phục vụ AI phân tích độ kích ứng và hiệu quả.
     */
    @Column(columnDefinition = "TEXT")
    private String ingredients;
    /**
     * Hướng dẫn sử dụng chi tiết để đạt hiệu quả tốt nhất.
     */
    @Column(name = "usage_instructions", columnDefinition = "TEXT")
    private String usageInstructions;
    /**
     * Danh sách các loại da phù hợp (Ví dụ: Da dầu, Da nhạy cảm).
     * Được convert thành String/Text[] trong DB để AI dễ dàng truy vấn gợi ý.
     */
    @Convert(converter = StringListConverter.class)
    @Column(name = "suitable_skin_types", length = 500)
    private List<String> suitableSkinTypes;
    /**
     * Các vấn đề về da sản phẩm có thể giải quyết (Ví dụ: Mụn, Lão hóa).
     */
    @Convert(converter = StringListConverter.class)
    @Column(name = "skin_concerns", length = 500)
    private List<String> skinConcerns;
    /**
     * Điểm đánh giá trung bình từ người dùng (Hệ số 5).
     */
    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;
    /**
     * Tổng số lượt đánh giá đã nhận.
     */
    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;
    /**
     * Tổng số lượng sản phẩm đã bán thành công.
     */
    @Column(name = "total_sold")
    @Builder.Default
    private Integer totalSold = 0;
    /**
     * Giá bán thấp nhất trong số các biến thể (dùng để hiển thị "Giá từ...").
     */
    @Column(name = "min_price", precision = 19, scale = 2)
    private BigDecimal minPrice;
    /**
     * Giá bán cao nhất trong số các biến thể.
     */
    @Column(name = "max_price", precision = 19, scale = 2)
    private BigDecimal maxPrice;
    /**
     * Trạng thái kinh doanh của sản phẩm.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    /**
     * Đánh dấu sản phẩm tiêu biểu để ưu tiên hiển thị ở trang chủ.
     */
    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;
    /**
     * Danh mục sản phẩm (N-1). Sử dụng FetchType.LAZY để tối ưu hiệu năng.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    /**
     * Thương hiệu sản phẩm (N-1).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;
    /**
     * Danh sách hình ảnh chi tiết của sản phẩm.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();
    /**
     * Các biến thể về dung tích hoặc màu sắc của sản phẩm.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods for managing bidirectional relationships
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    public void removeVariant(ProductVariant variant) {
        variants.remove(variant);
        variant.setProduct(null);
    }
}
