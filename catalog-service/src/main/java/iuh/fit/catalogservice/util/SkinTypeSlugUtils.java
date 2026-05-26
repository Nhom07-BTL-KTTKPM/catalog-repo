package iuh.fit.catalogservice.util;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SkinTypeSlugUtils {

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Biến một chuỗi Tiếng Việt có dấu thành Slug chữ thường, không dấu, nối bằng gạch ngang
     * Ví dụ: "Da mất độ đàn hồi" -> "da-mat-do-dan-hoi"
     */
    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // 1. Đưa về chuẩn NFC để tránh lỗi Unicode tổ hợp
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFC);
        
        // 2. Chuyển thành chữ thường và xóa khoảng trắng thừa ở 2 đầu
        String lowerCase = normalized.toLowerCase().trim();
        
        // 3. Tách dấu ra khỏi chữ cốt và xóa dấu (Thay đ thành d)
        String decompounded = Normalizer.normalize(lowerCase, Normalizer.Form.NFD);
        String withoutDiacritics = DIACRITICS_PATTERN.matcher(decompounded).replaceAll("");
        String withSimpleD = withoutDiacritics.replace('đ', 'd');
        
        // 4. Thay thế khoảng trắng và các ký tự đặc biệt bằng dấu gạch ngang
        String slug = withSimpleD.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");

        // 5. Chuẩn hóa lại dấu gạch ngang nếu input đã có sẵn dạng slug
        slug = slug.replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
        
        return slug;
    }

    /**
     * Biến một danh sách Tiếng Việt thành một chuỗi duy nhất bọc bằng dấu chấm phẩy để lưu DB
     * Ví dụ: ["Da dầu", "Da mụn"] -> ";da-dau;da-mun;"
     */
    public static String toDbString(List<String> rawSkinTypes) {
        if (rawSkinTypes == null || rawSkinTypes.isEmpty()) {
            return "";
        }
        return rawSkinTypes.stream()
                .map(SkinTypeSlugUtils::toSlug)
                .filter(slug -> !slug.isEmpty())
                .collect(Collectors.joining(";", ";", ";"));
    }
}