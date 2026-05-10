package iuh.fit.catalogservice.repo;

import iuh.fit.catalogservice.entity.Brand;
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
 * Repository for Brand entity
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    /**
     * Tìm kiếm một thương hiệu cụ thể dựa trên đường dẫn SEO (slug).
     * @param slug Đường dẫn duy nhất của thương hiệu.
     * @return Optional chứa Brand nếu tìm thấy, hoặc rỗng nếu không tồn tại.
     */
    Optional<Brand> findBySlug(String slug);

    /**
     * Lấy danh sách tất cả các thương hiệu ĐANG HOẠT ĐỘNG (isActive = true).
     * Kết quả được sắp xếp theo bảng chữ cái (A-Z) của tên thương hiệu.
     */
    List<Brand> findByIsActiveTrueOrderByNameAsc();

    /**
     * Lấy danh sách phân trang tất cả các thương hiệu trong hệ thống,
     * bao gồm cả các thương hiệu đã bị ẩn (isActive = false).
     * Thường dùng cho trang quản trị (Admin Panel).
     */
    Page<Brand> findAll(Pageable pageable);

    /**
     * Kiểm tra xem một slug đã tồn tại trong database hay chưa.
     * Rất hữu ích khi tạo mới hoặc cập nhật thương hiệu để tránh lỗi Duplicate Key.
     */
    boolean existsBySlug(String slug);

    /**
     * Tìm kiếm thương hiệu theo từ khóa (hỗ trợ tìm kiếm tương đối LIKE).
     * @param keyword Từ khóa người dùng nhập vào.
     * @return Danh sách thương hiệu đang hoạt động có tên chứa từ khóa (không phân biệt hoa thường).
     */
    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND b.isActive = true")
    List<Brand> searchByName(@Param("keyword") String keyword);

    /**
     * Lọc danh sách các thương hiệu dựa trên quốc gia xuất xứ.
     * @param originCountry Tên quốc gia (VD: "Hàn Quốc", "Pháp").
     * @return Danh sách thương hiệu đang hoạt động thuộc quốc gia đó.
     */
    List<Brand> findByOriginCountryAndIsActiveTrue(String originCountry);
}

