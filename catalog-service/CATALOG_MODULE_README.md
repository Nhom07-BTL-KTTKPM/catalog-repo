# Catalog Service - Product Module

Module quản lý sản phẩm mỹ phẩm cho hệ thống e-commerce.

## Cấu trúc Database

Module này bao gồm 5 bảng chính:

### 1. Categories (Danh mục sản phẩm)
- Hỗ trợ cấu trúc phân cấp (parent-child)
- Thuộc tính: id, name, slug, description, imageUrl, parentId, displayOrder, isActive
- Indexes: slug, parent_id

### 2. Brands (Thương hiệu)
- Quản lý thông tin các thương hiệu mỹ phẩm
- Thuộc tính: id, name, slug, description, logoUrl, originCountry, websiteUrl, isActive
- Indexes: slug

### 3. Products (Sản phẩm)
- Sản phẩm chính với đầy đủ thông tin
- Hỗ trợ AI recommendation qua fields: suitableSkinTypes, skinConcerns
- Thuộc tính: id, name, slug, description, ingredients, usageInstructions, suitableSkinTypes, skinConcerns, averageRating, totalReviews, totalSold, minPrice, maxPrice, isActive, isFeatured, categoryId, brandId
- Indexes: slug, category_id, brand_id, is_active, is_featured

### 4. Product_Images (Ảnh sản phẩm)
- Quản lý nhiều ảnh cho mỗi sản phẩm
- Thuộc tính: id, productId, url, publicId, altText, displayOrder, isPrimary
- Indexes: product_id, is_primary

### 5. Product_Variants (Biến thể sản phẩm)
- Các phiên bản khác nhau của sản phẩm (màu sắc, kích thước, v.v.)
- Thuộc tính: id, productId, sku, variantName, price, originalPrice, stockQuantity, sold, imageUrl, isActive
- Indexes: product_id, sku, is_active

## API Endpoints

### Category APIs

#### Public Endpoints
- `GET /api/categories` - Lấy tất cả danh mục (có phân trang)
- `GET /api/categories/{id}` - Lấy danh mục theo ID
- `GET /api/categories/slug/{slug}` - Lấy danh mục theo slug
- `GET /api/categories/active` - Lấy danh mục đang active
- `GET /api/categories/root` - Lấy danh mục gốc (không có parent)
- `GET /api/categories/children/{parentId}` - Lấy danh mục con
- `GET /api/categories/search?keyword={keyword}` - Tìm kiếm danh mục

#### Admin Endpoints (Yêu cầu ROLE_ADMIN)
- `POST /api/categories` - Tạo danh mục mới
- `PUT /api/categories/{id}` - Cập nhật danh mục
- `DELETE /api/categories/{id}` - Xóa danh mục

### Brand APIs

#### Public Endpoints
- `GET /api/brands` - Lấy tất cả thương hiệu (có phân trang)
- `GET /api/brands/{id}` - Lấy thương hiệu theo ID
- `GET /api/brands/slug/{slug}` - Lấy thương hiệu theo slug
- `GET /api/brands/active` - Lấy thương hiệu đang active
- `GET /api/brands/search?keyword={keyword}` - Tìm kiếm thương hiệu
- `GET /api/brands/country/{country}` - Lấy thương hiệu theo quốc gia

#### Admin Endpoints (Yêu cầu ROLE_ADMIN)
- `POST /api/brands` - Tạo thương hiệu mới
- `PUT /api/brands/{id}` - Cập nhật thương hiệu
- `DELETE /api/brands/{id}` - Xóa thương hiệu

### Product APIs

#### Public Endpoints
- `GET /api/products` - Lấy tất cả sản phẩm active (có phân trang)
- `GET /api/products/{id}` - Lấy sản phẩm theo ID
- `GET /api/products/slug/{slug}` - Lấy sản phẩm theo slug
- `GET /api/products/featured` - Lấy sản phẩm nổi bật
- `GET /api/products/category/{categoryId}` - Lấy sản phẩm theo danh mục
- `GET /api/products/brand/{brandId}` - Lấy sản phẩm theo thương hiệu
- `GET /api/products/category/{categoryId}/brand/{brandId}` - Lọc theo cả danh mục và thương hiệu
- `GET /api/products/search?keyword={keyword}` - Tìm kiếm sản phẩm
- `GET /api/products/best-selling` - Sản phẩm bán chạy nhất
- `GET /api/products/top-rated` - Sản phẩm đánh giá cao nhất
- `GET /api/products/skin-type/{skinType}` - Lọc theo loại da
- `GET /api/products/skin-concern/{skinConcern}` - Lọc theo vấn đề da
- `GET /api/products/price-range?minPrice={min}&maxPrice={max}` - Lọc theo giá

#### Admin Endpoints (Yêu cầu ROLE_ADMIN)
- `POST /api/products` - Tạo sản phẩm mới
- `PUT /api/products/{id}` - Cập nhật sản phẩm
- `DELETE /api/products/{id}` - Xóa sản phẩm
- `POST /api/products/{id}/update-price-range` - Cập nhật khoảng giá từ variants

## Request/Response Examples

### Tạo Category
```json
POST /api/categories
{
  "name": "Skincare",
  "slug": "skincare",
  "description": "Các sản phẩm chăm sóc da",
  "imageUrl": "https://cloudinary.com/...",
  "parentId": null,
  "displayOrder": 1,
  "isActive": true
}
```

### Tạo Brand
```json
POST /api/brands
{
  "name": "The Ordinary",
  "slug": "the-ordinary",
  "description": "Thương hiệu mỹ phẩm chất lượng cao",
  "logoUrl": "https://cloudinary.com/...",
  "originCountry": "Canada",
  "websiteUrl": "https://theordinary.com",
  "isActive": true
}
```

### Tạo Product
```json
POST /api/products
{
  "name": "Vitamin C Serum 23%",
  "slug": "vitamin-c-serum-23",
  "description": "Serum vitamin C cao cấp",
  "ingredients": "Ascorbic Acid 23%, Hyaluronic Acid...",
  "usageInstructions": "Sử dụng 1 lần/ngày vào buổi sáng",
  "suitableSkinTypes": ["oily", "combination", "normal"],
  "skinConcerns": ["dark-spots", "dull-skin", "uneven-tone"],
  "categoryId": "uuid-category",
  "brandId": "uuid-brand",
  "isActive": true,
  "isFeatured": false
}
```

## Cấu hình Database

Database sử dụng PostgreSQL. Cấu hình trong file `.env`:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/catalog_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

## Chạy Service

```bash
# Build project
./mvn clean install

# Run service
./mvn spring-boot:run
```

## Features Đặc biệt

### 1. AI Recommendation Support
- Fields `suitableSkinTypes` và `skinConcerns` hỗ trợ AI phân tích và gợi ý sản phẩm
- Lưu trữ dưới dạng List<String> và convert sang comma-separated string trong DB

### 2. Price Range Auto-Update
- Tự động cập nhật minPrice/maxPrice từ variants
- Endpoint: `POST /api/products/{id}/update-price-range`

### 3. Hierarchical Categories
- Hỗ trợ cấu trúc danh mục đa cấp
- Dễ dàng query root categories và child categories

### 4. Image Management
- Hỗ trợ nhiều ảnh cho mỗi sản phẩm
- Đánh dấu ảnh chính (isPrimary)
- Thứ tự hiển thị (displayOrder)

### 5. SEO-Friendly
- Tất cả entity đều có slug field
- Alt text cho images

## Notes

1. Tất cả ID đều sử dụng UUID
2. Timestamps (createdAt, updatedAt) được tự động quản lý bởi Hibernate
3. Lazy loading được áp dụng cho các relationships để tối ưu performance
4. Service layer sử dụng @Transactional để đảm bảo data consistency
5. Security: Admin endpoints yêu cầu ROLE_ADMIN authority

