package iuh.fit.catalogservice.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * StringListConverter - Bộ chuyển đổi dữ liệu giữa List (Java) và String (Database).
 *
 * Áp dụng nguyên tắc Single Responsibility (SOLID):
 * - Chịu trách nhiệm duy nhất là chuyển đổi kiểu dữ liệu mảng chuỗi thành một chuỗi văn bản
 *   phân tách bằng dấu phẩy để lưu trữ trong các cột SQL không hỗ trợ kiểu Array mặc định.
 *
 * Cách hoạt động:
 * - Khi lưu (Write): List ["A", "B"] -> String "A,B"
 * - Khi đọc (Read): String "A,B" -> List ["A", "B"]
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    /**
     * Ký tự phân cách giữa các phần tử trong chuỗi database.
     */
    private static final String DELIMITER = ",";
    /**
     * Chuyển đổi từ thuộc tính thực thể (Entity Attribute) sang dữ liệu cột (Database Column).
     * Được gọi tự động bởi JPA khi thực hiện các lệnh INSERT hoặc UPDATE.
     *
     * @param attribute Danh sách các chuỗi (ví dụ: các loại da phù hợp).
     * @return Chuỗi văn bản đã nối bằng dấu phẩy, hoặc null nếu danh sách trống.
     */
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return String.join(DELIMITER, attribute);
    }
    /**
     * Chuyển đổi từ dữ liệu cột (Database Column) sang thuộc tính thực thể (Entity Attribute).
     * Được gọi tự động bởi JPA khi thực hiện các lệnh SELECT.
     *
     * @param dbData Chuỗi văn bản từ database (ví dụ: "Oily,Sensitive").
     * @return Danh sách các chuỗi đã được tách, hoặc danh sách trống nếu dữ liệu null.
     */
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Tách chuỗi dựa trên ký tự DELIMITER và chuyển về List
        return Arrays.asList(dbData.split(DELIMITER));
    }
}

