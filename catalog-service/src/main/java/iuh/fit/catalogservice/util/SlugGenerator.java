package iuh.fit.catalogservice.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Lớp tiện ích để tạo slug thân thiện với SEO từ tên sản phẩm
 */
@UtilityClass
@Slf4j
public class SlugGenerator {

    private static final Pattern PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Tạo một slug từ tên sản phẩm.
     * Loại bỏ dấu tiếng Việt, chuyển thành chữ thường, thay thế khoảng trắng bằng dấu gạch ngang.
     *
     * @param input chuỗi đầu vào (tên sản phẩm)
     * @return slug đã được tạo
     */
    public static String generate(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Dữ liệu đầu vào không được để trống hoặc null");
        }

        // Chuẩn hóa và loại bỏ dấu (Ví dụ: ả, ế,...)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = PATTERN.matcher(normalized).replaceAll("");

        // Chuyển sang chữ thường, chỉ giữ lại chữ cái, số và dấu gạch ngang
        String slug = withoutAccents.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")  // Thay thế một hoặc nhiều ký tự không phải chữ/số bằng dấu gạch ngang
                .replaceAll("^-|-$", "");         // Loại bỏ dấu gạch ngang ở đầu và cuối chuỗi

        log.debug("Đã tạo slug: '{}' từ đầu vào: '{}'", slug, input);
        return slug;
    }

    /**
     * Tạo slug duy nhất bằng cách thêm số thứ tự nếu cần thiết.
     * Được sử dụng để xử lý các slug bị trùng lặp.
     *
     * @param baseSlug slug cơ bản ban đầu
     * @param counter số thứ tự để thêm vào (1, 2, 3, ...)
     * @return slug duy nhất đã được thêm số thứ tự
     */
    public static String generateUnique(String baseSlug, int counter) {
        if (counter <= 0) {
            return baseSlug;
        }
        return baseSlug + "-" + counter;
    }
}