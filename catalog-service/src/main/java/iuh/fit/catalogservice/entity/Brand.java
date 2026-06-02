package iuh.fit.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Brand entity - cosmetic brands (L'Oreal, Innisfree, The Ordinary, etc.)
 */
@Entity
@Table(name = "brands", indexes = {
        @Index(name = "idx_brand_slug", columnList = "slug")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    /**
     * Khóa chính định danh thương hiệu.
     * Sử dụng UUID để đảm bảo tính duy nhất trên toàn hệ thống và che giấu quy mô dữ liệu.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Chuỗi định danh phục vụ SEO và URL (Ví dụ: the-ordinary).
     * Ràng buộc Unique để đảm bảo không trùng lặp đường dẫn.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    /**
     * Thông tin chi tiết, câu chuyện hoặc châm ngôn của thương hiệu.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Đường dẫn chứa tập tin Logo thương hiệu.
     * Thường trỏ đến các dịch vụ lưu trữ đám mây Cloudinary.
     */
    @Column(name = "logo_url", length = 500)
    private String logoUrl;
    /**
     * Quốc gia xuất xứ của thương hiệu (Ví dụ: Pháp, Hàn Quốc, Mỹ).
     * Hỗ trợ bộ lọc tìm kiếm sản phẩm theo nguồn gốc.
     */
    @Column(name = "origin_country", length = 100)
    private String originCountry;
    /**
     * Liên kết dẫn đến website chính thức của nhãn hàng để khách hàng tham khảo thêm.
     */
    @Column(name = "website_url", length = 500)
    private String websiteUrl;
    /**
     * Trạng thái hợp tác/hoạt động của thương hiệu trên hệ thống.
     * False nếu ngừng kinh doanh các sản phẩm thuộc nhãn hàng này.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    /**
     * Thời điểm dữ liệu thương hiệu được khởi tạo trong hệ thống.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    /**
     * Thời điểm cập nhật thông tin thương hiệu gần nhất.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

