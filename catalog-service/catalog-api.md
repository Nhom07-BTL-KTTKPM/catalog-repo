# Tài liệu REST API - Module Catalog

Phiên bản: 1.0

Base path: `/api/v1/catalog`

Mô tả: tài liệu này mô tả đầy đủ REST API cho các controllers chính của module Catalog: `BrandController`, `CategoryController`, `ProductController`, `ProductVariantController`.

## Mục lục

- [1. Tổng quan](#1-tổng-quan)
- [2. Quy ước chung](#2-quy-ước-chung)
- [3. DTO chính](#3-dto-chính)
- [4. Brand APIs](#4-brand-apis)
- [5. Category APIs](#5-category-apis)
- [6. Product APIs](#6-product-apis)
- [7. Product Variant APIs](#7-product-variant-apis)
- [8. Validation errors & xử lý lỗi](#8-validation-errors--xử-lý-lỗi)
- [9. Lưu ý nghiệp vụ](#9-lưu-ý-nghiệp-vụ)
- [10. Mã lỗi phổ biến](#10-mã-lỗi-phổ-biến)

---

## 1. Tổng quan

- Framework: Spring Boot
- Base path toàn cục: `/api/v1/catalog`
- Response thành công: trả trực tiếp DTO (hoặc Page<DTO>)
- Response lỗi: chuẩn hoá theo cấu trúc `ErrorMessage` (xem phần 2)

## 2. Quy ước chung

### 2.1 Request headers

- `Content-Type: application/json` (khi có body)
- `Accept: application/json`
- `Authorization: Bearer <token>` — cần cho các endpoint yêu cầu quyền. Các role được sử dụng trong controller: `ROLE_ADMIN`, `ROLE_EMPLOYEE`.

### 2.2 Cấu trúc lỗi chuẩn (`ErrorMessage`)

```json
{
	"statusCode": 400,
	"timestamp": "2026-05-22T10:00:00",
	"message": "Chi tiết thông điệp lỗi",
	"description": "Ngữ cảnh lỗi"
}
```

### 2.3 Pagination params (được hỗ trợ ở các endpoint trả `Page`)

- `page` (integer) — trang, mặc định 0
- `size` (integer) — kích thước trang, mặc định 20 (hoặc theo `@PageableDefault` trong controller ghi rõ 20 hoặc 10)
- `sort` (string) — ví dụ `sort=createdAt,desc` hoặc `sort=name,asc`

Trong tài liệu, nếu endpoint trả `Page`, tôi sẽ ghi rõ `page`, `size`, `sort` và giá trị mặc định như đã thấy trong controller (`size=20`, `featured` có `size=10`).

## 3. DTO chính

Các DTO chính được sử dụng trong controller đã đọc từ source. Bảng mô tả dưới đây tóm tắt các field quan trọng.

### 3.1 `BrandRequest` (Request)

| Field | Kiểu | Bắt buộc | Ràng buộc | Mô tả |
|---|---:|---:|---|---|
| `name` | string | Có | not blank, max 255 | Tên thương hiệu |
| `slug` | string | Có | not blank, max 255 | Chuỗi slug dùng URL |
| `description` | string | Không | - | Mô tả thương hiệu |
| `logoUrl` | string | Không | - | URL logo |
| `originCountry` | string | Không | max 100 | Quốc gia nguồn gốc |
| `websiteUrl` | string | Không | - | Trang web chính thức |
| `isActive` | boolean | Có | not null | Trạng thái hoạt động |

### 3.2 `BrandStatusRequest` (Request)

| Field | Kiểu | Bắt buộc | Ràng buộc | Mô tả |
|---|---:|---:|---|---|
| `isActive` | boolean | Có | not null | Bật/tắt trạng thái hiển thị của brand |

### 3.3 `BrandResponse` (Response)

| Field | Kiểu | Mô tả |
|---|---:|---|
| `id` | UUID | Id thương hiệu |
| `name` | string | Tên |
| `slug` | string | Slug |
| `description` | string | Mô tả |
| `logoUrl` | string | URL logo |
| `originCountry` | string | Quốc gia |
| `websiteUrl` | string | URL |
| `isActive` | boolean | Trạng thái |
| `createdAt` | datetime | Thời điểm tạo |
| `updatedAt` | datetime | Thời điểm cập nhật |

### 3.4 `CategoryRequest` (Request)

| Field | Kiểu | Bắt buộc | Ràng buộc | Mô tả |
|---|---:|---:|---|---|
| `name` | string | Có | not blank, max 255 | Tên danh mục |
| `slug` | string | Có | not blank, max 255 | Slug |
| `description` | string | Không | - | Mô tả |
| `imageUrl` | string | Không | - | Ảnh đại diện |
| `parentId` | UUID | Không | - | Id danh mục cha |
| `isActive` | boolean | Có | not null | Trạng thái |

### 3.5 `CategoryResponse` (Response)

| Field | Kiểu | Mô tả |
|---|---:|---|
| `id` | UUID | Id danh mục |
| `name` | string | Tên |
| `slug` | string | Slug |
| `description` | string | Mô tả |
| `imageUrl` | string | Ảnh |
| `parentId` | UUID | Id cha |
| `isActive` | boolean | Trạng thái |
| `createdAt` | datetime | Thời điểm tạo |
| `updatedAt` | datetime | Thời điểm cập nhật |

### 3.6 `ProductRequest` (Request)

| Field | Kiểu | Bắt buộc | Ràng buộc | Mô tả |
|---|---:|---:|---|---|
| `name` | string | Có | not blank, max 500 |
| `slug` | string | Không | max 500 (tự sinh nếu thiếu) |
| `description` | string | Không | - |
| `ingredients` | string | Không | - |
| `usageInstructions` | string | Không | - |
| `suitableSkinTypes` | List<String> | Không | - |
| `skinConcerns` | List<String> | Không | - |
| `variants` | List<ProductVariantRequest> | Không | @Valid |
| `images` | List<ProductImageRequest> | Không | @Valid |
| `categoryId` | UUID | Có | not null |
| `brandId` | UUID | Có | not null |
| `isActive` | boolean | Không | default true |
| `isFeatured` | boolean | Không | default false |

### 3.7 `ProductResponse` (Response)

| Field | Kiểu | Mô tả |
|---|---:|---|
| `id` | UUID | Id sản phẩm |
| `name` | string | Tên |
| `slug` | string | Slug |
| `description` | string | Mô tả |
| `ingredients` | string | Thành phần |
| `usageInstructions` | string | HDSD |
| `suitableSkinTypes` | List<String> | Các loại da phù hợp |
| `skinConcerns` | List<String> | Vấn đề da |
| `averageRating` | double | Đánh giá trung bình |
| `totalReviews` | int | Số review |
| `totalSold` | int | Đã bán |
| `minPrice` | BigDecimal | Giá min trong variants |
| `maxPrice` | BigDecimal | Giá max |
| `isActive` | boolean | Trạng thái |
| `isFeatured` | boolean | Nổi bật |
| `categoryId` | UUID | Id danh mục |
| `categoryName` | string | Tên danh mục |
| `brandId` | UUID | Id brand |
| `brandName` | string | Tên brand |
| `brandLogoUrl` | string | Logo brand |
| `images` | List<ProductImageResponse> | Ảnh |
| `variants` | List<ProductVariantResponse> | Biến thể |
| `createdAt` | datetime | Tạo lúc |
| `updatedAt` | datetime | Cập nhật |

### 3.8 `ProductVariantRequest` / `ProductVariantResponse`

Request đã có các ràng buộc (sku not blank, variantName not blank, price > 0, stockQuantity not null). Response có fields: `id`, `productId`, `sku`, `variantName`, `price`, `originalPrice`, `stockQuantity`, `sold`, `imageUrl`, `isActive`, `createdAt`, `updatedAt`.

---

## 4. Brand APIs

Base path: `/api/v1/catalog/brands`

### 4.1 Tạo Brand

- Method: `POST`
- URL: `/api/v1/catalog/brands`
- Roles: `ROLE_ADMIN` (theo `@PreAuthorize`)
- Headers: `Content-Type: application/json`, `Authorization: Bearer <token>`

Request body: `BrandRequest` (xem bảng DTO)

Validation rules: `name`, `slug` not blank; `isActive` not null.

Success: `201 Created` với body `BrandResponse`.

Ví dụ request:

```json
{
  "name": "L'Oréal Paris",
  "slug": "loreal-paris",
  "description": "Thương hiệu mỹ phẩm nổi tiếng với các sản phẩm chăm sóc da, trang điểm và chăm sóc tóc.",
  "logoUrl": "https://example.com/logos/loreal-paris.png",
  "originCountry": "France",
  "websiteUrl": "https://www.lorealparis.com",
  "isActive": true
}
```

Ví dụ response (201):

```json
{
    "id": "ee365597-31cf-422e-961c-532e716568e3",
    "name": "L'Oréal Paris",
    "slug": "loreal-paris",
    "description": "Thương hiệu mỹ phẩm nổi tiếng với các sản phẩm chăm sóc da, trang điểm và chăm sóc tóc.",
    "logoUrl": "https://example.com/logos/loreal-paris.png",
    "originCountry": "France",
    "websiteUrl": "https://www.lorealparis.com",
    "isActive": true,
    "createdAt": null,
    "updatedAt": null
}
```

Errors:

- `400 Bad Request` cho validation errors. Body `ErrorMessage`.
- `500 Internal Server Error` cho lỗi hệ thống.

### 4.2 Cập nhật Brand

- Method: `PUT`
- URL: `/api/v1/catalog/brands/{id}`
- Roles: `ROLE_ADMIN`
- Path var: `id` (UUID)
- Body: `BrandRequest`
- Success: `200 OK` với `BrandResponse`
- Errors: `400`, `404`, `500`
Ví dụ request:

```json
{
  "name": "L'Oréal Paris",
  "slug": "loreal-paris",
  "description": "Thương hiệu mỹ phẩm nổi tiếng với các sản phẩm chăm sóc da, trang điểm và chăm sóc tóc.",
  "logoUrl": "https://logos-world.net/wp-content/uploads/2020/04/LOreal-Emblem.png",
  "originCountry": "France",
  "websiteUrl": "https://www.lorealparis.com",
  "isActive": true
}
```

### 4.3 Lấy Brand theo id

- Method: `GET`
- URL: `/api/v1/catalog/brands/{id}`
- Public
- Success: `200 OK` với `BrandResponse`
- Errors: `404` nếu không tìm thấy
Ví dụ reponse:
```json
{
    "id": "ee365597-31cf-422e-961c-532e716568e3",
    "name": "L'Oréal Paris",
    "slug": "loreal-paris",
    "description": "Thương hiệu mỹ phẩm nổi tiếng với các sản phẩm chăm sóc da, trang điểm và chăm sóc tóc.",
    "logoUrl": "https://logos-world.net/wp-content/uploads/2020/04/LOreal-Emblem.png",
    "originCountry": "France",
    "websiteUrl": "https://www.lorealparis.com",
    "isActive": true,
    "createdAt": "2026-05-22T11:33:01.400157",
    "updatedAt": "2026-05-22T11:43:53.263762"
}
```
### 4.4 Lấy Brand theo slug

- Method: `GET`
- URL: `/api/v1/catalog/brands/slug/{slug}`
- Public
Ví dụ reponse:
```json
{
    "id": "ee365597-31cf-422e-961c-532e716568e3",
    "name": "L'Oréal Paris",
    "slug": "loreal-paris",
    "description": "Thương hiệu mỹ phẩm nổi tiếng với các sản phẩm chăm sóc da, trang điểm và chăm sóc tóc.",
    "logoUrl": "https://logos-world.net/wp-content/uploads/2020/04/LOreal-Emblem.png",
    "originCountry": "France",
    "websiteUrl": "https://www.lorealparis.com",
    "isActive": true,
    "createdAt": "2026-05-22T11:33:01.400157",
    "updatedAt": "2026-05-22T11:43:53.263762"
}
```
### 4.5 Lấy danh sách Brand (pagination)

- Method: `GET`
- URL: `/api/v1/catalog/brands`
- Query params: `page` (default 0), `size` (default 20), `sort`
vd: /brands?page=1&size=2&sort=name,asc
- Public
- Success: `200 OK` với `Page<BrandResponse>`
vd trang 1: /api/v1/catalog/brands?page=0&size=2

Ví dụ reponse:
```json
{
    "content": [
        {
            "id": "2df8771c-f60c-46ce-ae4f-65a97c67b8f7",
            "name": "The Ordinary",
            "slug": "the-ordinary",
            "description": "Thương hiệu mỹ phẩm tối giản từ Canada, tập trung vào thành phần hoạt tính thuần khiết với giá cả hợp lý.",
            "logoUrl": "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=200",
            "originCountry": "Canada",
            "websiteUrl": "https://theordinary.com",
            "isActive": true,
            "createdAt": "2026-05-21T10:09:12.596052",
            "updatedAt": "2026-05-21T10:09:12.596052"
        },
        {
            "id": "d37b01b2-fdd1-40e1-8665-3334a15d0e61",
            "name": "Innisfree",
            "slug": "innisfree",
            "description": "Thương hiệu mỹ phẩm thiên nhiên từ đảo Jeju, Hàn Quốc. Sử dụng nguyên liệu hữu cơ và thân thiện với môi trường.",
            "logoUrl": "https://images.unsplash.com/photo-1591360236480-4ed861025fa1?w=200",
            "originCountry": "Hàn Quốc",
            "websiteUrl": "https://innisfree.com",
            "isActive": true,
            "createdAt": "2026-05-21T10:09:12.60748",
            "updatedAt": "2026-05-21T10:09:12.60748"
        }
    ],
    "empty": false,
    "first": true,
    "last": false,
    "number": 0,
    "numberOfElements": 2,
    "pageable": {
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 2,
        "paged": true,
        "sort": [],
        "unpaged": false
    },
    "size": 2,
    "sort": [],
    "totalElements": 4,
    "totalPages": 2
}
```
vd trang 2: /api/v1/catalog/brands?page=1&size=2
### 4.6 Lấy danh sách brand active

- Method: `GET`
- URL: `/api/v1/catalog/brands/active`
- Public — trả `List<BrandResponse>`
Ví dụ reponse:
```json
{
        "id": "0f686818-17ec-44e1-8cce-1fbf2005826c",
        "name": "CeraVe",
        "slug": "cerave",
        "description": "Thương hiệu dược mỹ phẩm từ Mỹ, được bác sĩ da liễu khuyên dùng với công thức chứa Ceramides và Hyaluronic Acid.",
        "logoUrl": "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=200",
        "originCountry": "Hoa Kỳ",
        "websiteUrl": "https://cerave.com",
        "isActive": true,
        "createdAt": "2026-05-21T10:09:12.609867",
        "updatedAt": "2026-05-21T10:09:12.609867"
    },
```
### 4.7 Cập nhật trạng thái Brand

- Method: `PATCH`
- URL: `/api/v1/catalog/brands/{id}/status`
- Roles: `ROLE_ADMIN`
- Body: `BrandStatusRequest`
- Success: `200 OK` với `BrandResponse`
- Ý nghĩa: chỉ cập nhật `isActive` để ẩn/hiện brand, không xóa dữ liệu khỏi database.
Ví dụ request:
```json
{
  "isActive": false
}
```
### 4.8 Tìm kiếm Brand

- Method: `GET`
- URL: `/api/v1/catalog/brands/search?keyword={keyword}`
- Public — trả `List<BrandResponse>`
vd: ?keyword=e

---

## 5. Category APIs

Base path: `/api/v1/catalog/categories`

### 5.1 Tạo Category

- Method: `POST`
- URL: `/api/v1/catalog/categories`
- Roles: `ROLE_ADMIN`
- Body: `CategoryRequest`
- Success: `201 Created` với `CategoryResponse`

Ví dụ request:

```json
{
	"name": "Haircare",
	"slug": "haircare",
	"description": "Sản phẩm chăm sóc tóc",
	"imageUrl": "https://cdn.example.com/categories/haircare.png",
	"parentId": null,
	"isActive": true
}
```
Ví dụ reponse:

```json
{
    "id": "7a147f72-70fe-4b90-8d3d-842a392245a5",
    "name": "Haircare",
    "slug": "haircare",
    "description": "Sản phẩm chăm sóc tóc",
    "imageUrl": "https://as2.ftcdn.net/v2/jpg/07/25/02/03/1000_F_725020314_zGtQmiZhaD0MDGduey4bORSOcOzWYbyY.jpg",
    "parentId": null,
    "isActive": true,
    "createdAt": null,
    "updatedAt": null
}
```
### 5.2 Cập nhật Category

- Method: `PUT`
- URL: `/api/v1/catalog/categories/{id}`
- Roles: `ROLE_ADMIN`
- Body: `CategoryRequest`
- Success: `200 OK` với `CategoryResponse`

### 5.3 Lấy Category theo id

- Method: `GET`
- URL: `/api/v1/catalog/categories/{id}`
- Public — `200 OK` hoặc `404`

### 5.4 Lấy theo slug

- Method: `GET`
- URL: `/api/v1/catalog/categories/slug/{slug}`

### 5.5 Lấy tất cả (pagination)

- Method: `GET`
- URL: `/api/v1/catalog/categories`
- Query params: `page` (0), `size` (20), `sort`
- Success: `200 OK` với `Page<CategoryResponse>`

### 5.6 Lấy danh sách active

- Method: `GET`
- URL: `/api/v1/catalog/categories/active`
- Public — `List<CategoryResponse>`

### 5.7 Lấy root categories

- Method: `GET`
- URL: `/api/v1/catalog/categories/root`
- Public — `List<CategoryResponse>`

### 5.8 Lấy summaries (dùng cho menu)

- Method: `GET`
- URL: `/api/v1/catalog/categories/summary`
- Public — `List<CategorySummaryResponse>`

### 5.9 Lấy root summaries

- Method: `GET`
- URL: `/api/v1/catalog/categories/summary/root`

### 5.10 Lấy children theo parentId

- Method: `GET`
- URL: `/api/v1/catalog/categories/children/{parentId}`
- Public — `List<CategoryResponse>`

### 5.11 Xóa Category

- Method: `DELETE`
- URL: `/api/v1/catalog/categories/{id}`
- Roles: `ROLE_ADMIN`
- Success: `204 No Content`

### 5.12 Tìm kiếm Category

- Method: `GET`
- URL: `/api/v1/catalog/categories/search?keyword={keyword}`
- Public — `List<CategoryResponse>`

---

## 6. Product APIs

Base path: `/api/v1/catalog/products`

### 6.1 Tạo Product

- Method: `POST`
- URL: `/api/v1/catalog/products`
- Roles: `ROLE_ADMIN`, `ROLE_EMPLOYEE` (theo `@PreAuthorize`)
- Body: `ProductRequest` (xem DTO)
- Success: `201 Created` với `ProductResponse`

Ví dụ request (rút gọn):

```json
{
	"name": "Hydrating Serum",
	"slug": "hydrating-serum",
	"description": "Serum dưỡng ẩm sâu",
	"categoryId": "6f1b2c3d-4e5f-6789-0abc-def123456789",
	"brandId": "e7a6f1d2-3c4b-4f9d-92a1-1a2b3c4d5e6f",
	"isActive": true,
	"isFeatured": false,
	"variants": [
		{
			"sku": "HS-01",
			"variantName": "30ml",
			"price": 250000,
			"originalPrice": 300000,
			"stockQuantity": 100,
			"imageUrl": "https://.../hs-01.png",
			"isActive": true
		}
	]
}
```

Validation rules: `name` not blank; `categoryId`, `brandId` not null; each variant must satisfy its constraints (sku, variantName, price>0, stockQuantity present).

### 6.2 Cập nhật Product

- Method: `PUT`
- URL: `/api/v1/catalog/products/{id}`
- Roles: `ROLE_ADMIN`, `ROLE_EMPLOYEE`
- Body: `ProductRequest`
- Success: `200 OK` with `ProductResponse`

### 6.3 Lấy Product theo id

- Method: `GET`
- URL: `/api/v1/catalog/products/{id}`
- Public — `200 OK` hoặc `404`

### 6.4 Lấy theo slug

- Method: `GET`
- URL: `/api/v1/catalog/products/slug/{slug}`

### 6.5 Danh sách sản phẩm (pagination)

- Method: `GET`
- URL: `/api/v1/catalog/products`
- Query params: `page` (0), `size` (20), `sort`
- Success: `200 OK` với `Page<ProductResponse>`

### 6.6 Lấy featured products

- Method: `GET`
- URL: `/api/v1/catalog/products/featured`
- Query params: `page` (0), `size` (10)

### 6.7 Lấy sản phẩm theo category/brand/combined

- Method: `GET`
- URL examples:
	- `/api/v1/catalog/products/category/{categoryId}`
	- `/api/v1/catalog/products/brand/{brandId}`
	- `/api/v1/catalog/products/category/{categoryId}/brand/{brandId}`
- Query params: `page`, `size`, `sort`

### 6.8 Search products

- Method: `GET`
- URL: `/api/v1/catalog/products/search?keyword={keyword}`
- Query params: `page`, `size`

### 6.9 Best-selling / Top-rated

- `/api/v1/catalog/products/best-selling`
- `/api/v1/catalog/products/top-rated`
- Both support pagination

### 6.10 Filter by skin type / concern / price range

- `/api/v1/catalog/products/skin-type/{skinType}`
- `/api/v1/catalog/products/skin-concern/{skinConcern}`
- `/api/v1/catalog/products/price-range?minPrice={minPrice}&maxPrice={maxPrice}`

### 6.11 Xóa Product

- Method: `DELETE`
- URL: `/api/v1/catalog/products/{id}`
- Roles: `ROLE_ADMIN`
- Success: `204 No Content`

### 6.12 Operational endpoints

- `POST /api/v1/catalog/products/{id}/update-price-range` — Roles: `ROLE_ADMIN`, used to recalc product min/max price from variants. Returns `200 OK`.
- `POST /api/v1/catalog/products/total-sold/increment` — Accepts list `ProductSoldUpdateRequest` to increment `totalSold` counters. Typically used by order service or batch jobs. Returns `200 OK`.

---

## 7. Product Variant APIs

Base path: `/api/v1/catalog/variants`

### 7.1 Lấy variant theo id

- Method: `GET`
- URL: `/api/v1/catalog/variants/{variantId}`
- Public — trả `ProductVariantResponse` hoặc `404`

(Hiện controller cung cấp endpoint GET theo id. Các APIs tạo/sửa variant được xử lý thông qua ProductController `variants` trong `ProductRequest`.)

---

## 8. Validation errors & xử lý lỗi

- Các lỗi validation (Bean Validation) trả `400 Bad Request` với `ErrorMessage` chứa danh sách field error trong `message`.
- Các lỗi nghiệp vụ (ví dụ không tìm thấy resource) trả `404 Not Found`.
- Lỗi quyền truy cập do Spring Security trả `403 Forbidden`.
- Lỗi hệ thống trả `500 Internal Server Error`.

Ví dụ lỗi validation:

```json
{
	"statusCode": 400,
	"timestamp": "2026-05-22T09:30:00",
	"message": "name: Category name is required; slug: Category slug is required",
	"description": "Validation failed"
}
```

## 9. Lưu ý nghiệp vụ

- `slug` có thể được tự sinh nếu không cung cấp; cần hợp nhất logic slug unique ở service.
- Các endpoint tạo/cập nhật có `@Valid` trên DTO, mọi ràng buộc Bean Validation phải tuân thủ.
- Các API admin yêu cầu `Authorization` và role tương ứng như đã mô tả.
- Khi thay đổi giá variant, cần cập nhật product `minPrice`/`maxPrice` thông qua endpoint `update-price-range` hoặc service transaction.
- `totalSold` thường chỉ được cập nhật bởi service order; endpoint `total-sold/increment` có thể được gọi bởi event hoặc worker.

## 10. Mã lỗi phổ biến

| HTTP Status | Tình huống | Ví dụ message |
|---|---|---|
| 400 | Validation / input sai | `name: Product name is required` |
| 400 | Business rule violation | `Price must be greater than 0` |
| 401 | Unauthorized | `Full authentication is required to access this resource` |
| 403 | Forbidden | `Access is denied` |
| 404 | Resource not found | `Không tìm thấy product` |
| 500 | Lỗi server | `Unexpected server error` |

---

Nếu bạn đồng ý, tôi sẽ lưu bản này vào `catalog-service/catalog-api.md` (đã chỉnh sửa). Nếu cần bổ sung các ví dụ response chi tiết hơn cho từng endpoint cụ thể hoặc thêm các endpoint nội bộ khác, hãy cho biết endpoint nào cần mở rộng.

