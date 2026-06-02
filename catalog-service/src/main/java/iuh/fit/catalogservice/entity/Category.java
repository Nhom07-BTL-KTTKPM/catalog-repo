package iuh.fit.catalogservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Category entity - hierarchical product categorization
 */
@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_slug", columnList = "slug"),
        @Index(name = "idx_category_parent_id", columnList = "parent_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    /**
     * Khóa chính định danh duy nhất cho mỗi danh mục.
     * Sử dụng UUID để tăng tính bảo mật và khả năng mở rộng hệ thống phân tán.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    /**
     * Tên hiển thị của danh mục (Ví dụ: Skincare, Serum).
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Đường dẫn thân thiện cho SEO (Ví dụ: skincare-lam-dep).
     * Phải là duy nhất để truy cập trực tiếp qua URL.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Đường dẫn hình ảnh đại diện cho danh mục (Lưu URL từ Cloudinary).
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Liên kết tự tham chiếu (Self-reference) để tạo cấu trúc cha-con.
     * Nếu parentId là null, đây là danh mục gốc (Root Category).
     */
    @Column(name = "parent_id")
    private UUID parentId;

    /**
     * Trạng thái hoạt động của danh mục.
     * Cho phép ẩn/hiện danh mục mà không cần xóa dữ liệu.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời điểm danh mục cập nhật thông tin lần cuối.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

