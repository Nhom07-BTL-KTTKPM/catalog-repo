package iuh.fit.catalogservice.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iuh.fit.catalogservice.entity.Brand;
import iuh.fit.catalogservice.entity.Category;
import iuh.fit.catalogservice.entity.Product;
import iuh.fit.catalogservice.entity.ProductImage;
import iuh.fit.catalogservice.entity.ProductVariant;
import iuh.fit.catalogservice.repo.BrandRepository;
import iuh.fit.catalogservice.repo.CategoryRepository;
import iuh.fit.catalogservice.repo.ProductRepository;
import iuh.fit.catalogservice.util.SkinTypeSlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            log.info("Dữ liệu đã tồn tại, bỏ qua bước khởi tạo.");
            return;
        }

        log.info("==========Tao du lieu mau cho Catalog Service...");

        // ========== 1. Khởi tạo Thương hiệu (5 Brands) ==========
        Brand theOrdinary = Brand.builder()
                .name("The Ordinary")
                .slug("the-ordinary")
                .description("Thương hiệu mỹ phẩm tối giản từ Canada, tập trung vào thành phần hoạt tính thuần khiết với giá cả hợp lý.")
                .originCountry("Canada")
                .logoUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/the-ordinary-logo-png_seeklogo-323086.png")
                .websiteUrl("https://theordinary.com")
                .isActive(true)
                .build();

        Brand innisfree = Brand.builder()
                .name("Innisfree")
                .slug("innisfree")
                .description("Thương hiệu mỹ phẩm thiên nhiên từ đảo Jeju, Hàn Quốc. Sử dụng nguyên liệu hữu cơ và thân thiện với môi trường.")
                .originCountry("Hàn Quốc")
                .logoUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/innisfree.jpg")
                .websiteUrl("https://innisfree.com")
                .isActive(true)
                .build();

        Brand cerave = Brand.builder()
                .name("CeraVe")
                .slug("cerave")
                .description("Thương hiệu dược mỹ phẩm từ Mỹ, được bác sĩ da liễu khuyên dùng với công thức chứa Ceramides và Hyaluronic Acid.")
                .originCountry("Hoa Kỳ")
                .logoUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/cerave.png")
                .websiteUrl("https://cerave.com")
                .isActive(true)
                .build();

        Brand larochePosay = Brand.builder()
                .name("La Roche-Posay")
                .slug("la-roche-posay")
                .description("Dược mỹ phẩm đến từ Pháp, nổi tiếng với các công thức dịu nhẹ cho da nhạy cảm và da dầu mụn.")
                .originCountry("Pháp")
                .logoUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/laroche.png")
                .websiteUrl("https://www.laroche-posay.vn")
                .isActive(true)
                .build();

        Brand cosrx = Brand.builder()
                .name("COSRX")
                .slug("cosrx")
                .description("Thương hiệu Hàn Quốc tập trung vào các giải pháp chăm sóc da tối giản, hiệu quả cho da mụn và da nhạy cảm.")
                .originCountry("Hàn Quốc")
                .logoUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/COSRX_Logo.jpg")
                .websiteUrl("https://www.cosrx.com")
                .isActive(true)
                .build();

        brandRepository.saveAll(List.of(theOrdinary, innisfree, cerave, larochePosay, cosrx));
        log.info("==========Da tao xong 5 thuong hieu");

        // ========== 2. Khởi tạo Danh mục (7 Categories) ==========
        Category skincare = Category.builder()
                .name("Chăm sóc da")
                .slug("skincare")
                .description("Các sản phẩm chăm sóc da mặt và cơ thể")
                .imageUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/chamsoccothe.png")
                .isActive(true)
                .build();
        categoryRepository.save(skincare);

        Category serum = Category.builder()
                .name("Serum & Tinh chất")
                .slug("serum")
                .description("Tinh chất dưỡng da cô đặc với thành phần hoạt tính cao")
                .imageUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/tinhchat.png")
                .parentId(skincare.getId())
                .isActive(true)
                .build();
        categoryRepository.save(serum);

        Category cleanser = Category.builder()
                .name("Sữa rửa mặt")
                .slug("cleanser")
                .description("Các sản phẩm làm sạch da mặt nhẹ nhàng")
                .imageUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/ruawmat.png")
                .parentId(skincare.getId())
                .isActive(true)
                .build();
        categoryRepository.save(cleanser);

        Category moisturizer = Category.builder()
                .name("Kem dưỡng ẩm")
                .slug("moisturizer")
                .description("Kem dưỡng ẩm cho da mặt và body")
                .imageUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/ruawmat.png")
                .isActive(true)
                .build();
        categoryRepository.save(moisturizer);

        Category toner = Category.builder()
                .name("Toner & Nước hoa hồng")
                .slug("toner")
                .description("Làm sạch sâu, cân bằng pH và chuẩn bị cho các bước dưỡng tiếp theo")
                .imageUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/Toner.jpg")
                .parentId(moisturizer.getId())
                .isActive(true)
                .build();
        categoryRepository.save(toner);

        Category sunscreen = Category.builder()
                .name("Kem chống nắng")
                .slug("sunscreen")
                .description("Bảo vệ da khỏi tia UVA/UVB và hỗ trợ chăm sóc da hằng ngày")
                .imageUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/kcn.jpg")
                .parentId(skincare.getId())
                .isActive(true)
                .build();
        categoryRepository.save(sunscreen);

        Category bodyCare = Category.builder()
                .name("Chăm sóc body")
                .slug("body-care")
                .description("Dưỡng thể, làm sáng và chăm sóc làn da toàn thân")
                .imageUrl("https://minhbucketttt.s3.ap-southeast-2.amazonaws.com/products/body.jpg")
                .parentId(moisturizer.getId())
                .isActive(true)
                .build();
        categoryRepository.save(bodyCare);

        log.info("==========Da tao xong 7 danh muc");

        // ========== 3. Khởi tạo Sản phẩm (14 Products) ==========

        // --- SẢN PHẨM 1: The Ordinary Niacinamide 10% + Zinc 1% ---
        Product niacinamide = createProduct(
                "The Ordinary Niacinamide 10% + Zinc 1%",
                "the-ordinary-niacinamide-10-zinc-1",
                "Serum giảm mụn và kiểm soát dầu nhờn với 10% Niacinamide và 1% Zinc PCA. Giúp thu nhỏ lỗ chân lông và cân bằng độ ẩm cho da.",
                "Aqua, Niacinamide, Pentylene Glycol, Zinc PCA, Dimethyl Isosorbide, Tamarindus Indica Seed Gum, Xanthan Gum, Isoceteth-20, Ethoxydiglycol, Phenoxyethanol, Chlorphenesin",
                "Thoa 2-3 giọt lên da mặt sạch, sáng và tối. Tránh vùng da quanh mắt. Có thể kết hợp với các serum khác.",
                List.of("Da dầu", "Da hỗn hợp", "Da mụn"),
                List.of("Mụn", "Lỗ chân lông to", "Dầu nhờn"),
                new BigDecimal("185000"),
                new BigDecimal("340000"),
                0.0,
                0,
                0,
                true,
                serum,
                theOrdinary
        );
        niacinamide.addImage(createImage("https://cdn11.bigcommerce.com/s-9xjhkkb3r4/images/stencil/1280x1280/products/1640/6502/the-ordinary-niacinamide-10-zinc-1-supersize-serum-30ml__54867.1688677021.jpg?c=2?imbypass=on", "ord_nia_001", "The Ordinary Niacinamide - Ảnh chính", true, 0));
        niacinamide.addImage(createImage("https://gbcosmeticsofficial.com/cdn/shop/products/THEORDINARYniacinamide_1_1445x.png?v=1680815688", "ord_nia_002", "The Ordinary Niacinamide - Ảnh chi tiết", false, 1));
        niacinamide.addImage(createImage("https://guo.vn/wp-content/uploads/2023/10/Thanh-phan-serum-the-ordinary-niacinamide-10-zinc-1.webp", "ord_nia_003", "The Ordinary Niacinamide - Ảnh thành phần", false, 2));
        niacinamide.addVariant(createVariant("ORD-NIA-30", "Chai 30ml", new BigDecimal("185000"), new BigDecimal("220000"), 150, 800, "https://feel22.com/cdn/shop/files/TheOrdinaryNiacinamide10_Zinc1_30ml.png?v=1757511561"));
        niacinamide.addVariant(createVariant("ORD-NIA-60", "Chai 60ml", new BigDecimal("340000"), new BigDecimal("390000"), 80, 450, "https://a.allegroimg.com/original/1175ee/35c873ac4af18d7dfb42e324d681/The-Ordinary-Niacinamide-10-Zinc-1-30-ml-serum-przeciw-przebarwieniom"));
        productRepository.save(niacinamide);

        // --- SẢN PHẨM 2: Innisfree Green Tea Seed Serum ---
        Product greenTeaSerum = createProduct(
                "Innisfree Green Tea Seed Serum",
                "innisfree-green-tea-seed-serum",
                "Tinh chất dưỡng ẩm từ hạt trà xanh Jeju giúp cấp ẩm sâu, làm dịu và bảo vệ da. Chiết xuất từ trà xanh tươi organic.",
                "Water, Glycerin, Camellia Sinensis Leaf Extract (16.1%), Propanediol, Pentylene Glycol, Betaine, Trehalose, Sodium Hyaluronate, Ceramide NP",
                "Sau bước làm sạch và toner, thoa 2-3 giọt serum đều khắp mặt. Vỗ nhẹ để thẩm thấu. Dùng sáng và tối.",
                List.of("Mọi loại da", "Da khô", "Da nhạy cảm"),
                List.of("Da khô", "Thiếu ẩm", "Da nhạy cảm"),
                new BigDecimal("420000"),
                new BigDecimal("560000"),
                0.0,
                0,
                0,
                true,
                serum,
                innisfree
        );
        greenTeaSerum.addImage(createImage("https://images.unsplash.com/photo-1556229010-aa9e5c46b319?w=600", "inn_gts_001", "Innisfree Green Tea Serum - Ảnh chính", true, 0));
        greenTeaSerum.addImage(createImage("https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=600", "inn_gts_002", "Innisfree Green Tea Serum - Ảnh sản phẩm", false, 1));
        greenTeaSerum.addVariant(createVariant("INN-GTS-80", "Chai 80ml", new BigDecimal("420000"), new BigDecimal("480000"), 120, 650, "https://images.unsplash.com/photo-1556229010-aa9e5c46b319?w=600"));
        greenTeaSerum.addVariant(createVariant("INN-GTS-160", "Chai 160ml (Tiết kiệm)", new BigDecimal("560000"), new BigDecimal("650000"), 60, 240, "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=600"));
        productRepository.save(greenTeaSerum);

        // --- SẢN PHẨM 3: CeraVe Hydrating Facial Cleanser ---
        Product ceravecleanser = createProduct(
                "CeraVe Hydrating Facial Cleanser",
                "cerave-hydrating-facial-cleanser",
                "Sữa rửa mặt dịu nhẹ với 3 loại Ceramides thiết yếu và Hyaluronic Acid. Làm sạch mà không làm khô da, phù hợp cho da khô đến da thường.",
                "Purified Water, Glycerin, Ceramide 1, Ceramide 3, Ceramide 6-II, Hyaluronic Acid, Cholesterol, Phytosphingosine, Behentrimonium Methosulfate",
                "Làm ướt mặt, cho 1-2 pump sữa rửa mặt lên tay và massage nhẹ nhàng. Rửa sạch với nước. Dùng sáng và tối.",
                List.of("Da khô", "Da thường", "Da nhạy cảm"),
                List.of("Da khô", "Làm sạch nhẹ nhàng"),
                new BigDecimal("210000"),
                new BigDecimal("450000"),
                0.0,
                0,
                0,
                true,
                cleanser,
                cerave
        );
        ceravecleanser.addImage(createImage("https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600", "cer_hfc_001", "CeraVe Cleanser - Ảnh chính", true, 0));
        ceravecleanser.addImage(createImage("https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=600", "cer_hfc_002", "CeraVe Cleanser - Ảnh chi tiết", false, 1));
        ceravecleanser.addImage(createImage("https://images.unsplash.com/photo-1616394584738-fc6e612e71b9?w=600", "cer_hfc_003", "CeraVe Cleanser - Ảnh kết cấu", false, 2));
        ceravecleanser.addVariant(createVariant("CER-HFC-237", "Chai 237ml", new BigDecimal("210000"), new BigDecimal("250000"), 200, 1500, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600"));
        ceravecleanser.addVariant(createVariant("CER-HFC-473", "Chai 473ml (Size lớn)", new BigDecimal("350000"), new BigDecimal("420000"), 100, 600, "https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=600"));
        ceravecleanser.addVariant(createVariant("CER-HFC-87", "Chai mini 87ml (Du lịch)", new BigDecimal("85000"), new BigDecimal("110000"), 80, 240, "https://images.unsplash.com/photo-1616394584738-fc6e612e71b9?w=600"));
        productRepository.save(ceravecleanser);

        // --- SẢN PHẨM 4: The Ordinary Hyaluronic Acid 2% + B5 ---
        Product hyaluronicAcid = createProduct(
                "The Ordinary Hyaluronic Acid 2% + B5",
                "the-ordinary-hyaluronic-acid-2-b5",
                "Serum cấp ẩm chuyên sâu với 3 dạng Hyaluronic Acid kết hợp Vitamin B5. Giúp da căng mọng, giữ ẩm tối ưu 24h.",
                "Aqua, Sodium Hyaluronate, Panthenol (Vitamin B5), Sodium Hyaluronate Crosspolymer, Pentylene Glycol, Polyacrylate Crosspolymer-6",
                "Thoa vài giọt lên da ẩm sau khi làm sạch. Vỗ nhẹ cho thấm. Dùng trước các bước dưỡng khác, sáng và tối.",
                List.of("Mọi loại da", "Da khô", "Da nhạy cảm"),
                List.of("Da khô", "Thiếu nước", "Lão hóa"),
                new BigDecimal("170000"),
                new BigDecimal("320000"),
                0.0,
                0,
                0,
                true,
                serum,
                theOrdinary
        );
        hyaluronicAcid.addImage(createImage("https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?w=600", "ord_ha_001", "The Ordinary HA 2% - Ảnh chính", true, 0));
        hyaluronicAcid.addImage(createImage("https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600", "ord_ha_002", "The Ordinary HA 2% - Ảnh sản phẩm", false, 1));
        hyaluronicAcid.addVariant(createVariant("ORD-HA-30", "Chai 30ml", new BigDecimal("170000"), new BigDecimal("210000"), 180, 890, "https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?w=600"));
        hyaluronicAcid.addVariant(createVariant("ORD-HA-60", "Chai 60ml", new BigDecimal("320000"), new BigDecimal("380000"), 90, 230, "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600"));
        productRepository.save(hyaluronicAcid);

        // --- SẢN PHẨM 5: Innisfree Volcanic Pore Clay Mask ---
        Product volcanicMask = createProduct(
                "Innisfree Volcanic Pore Clay Mask",
                "innisfree-volcanic-pore-clay-mask",
                "Mặt nạ đất sét núi lửa Jeju giúp hút sạch bã nhờn, bụi bẩn trong lỗ chân lông. Làm sạch sâu và se khít lỗ chân lông hiệu quả.",
                "Water, Titanium Dioxide, Glycerin, Volcanic Ash (7,615ppm), Bentonite, Kaolin, Caprylic/Capric Triglyceride, Trehalose, Tocopherol",
                "Thoa đều lên da mặt sạch, tránh vùng mắt và môi. Để 10-15 phút rồi rửa sạch bằng nước ấm. Dùng 1-2 lần/tuần.",
                List.of("Da dầu", "Da hỗn hợp", "Da mụn"),
                List.of("Lỗ chân lông to", "Dầu nhờn", "Mụn đầu đen"),
                new BigDecimal("250000"),
                new BigDecimal("350000"),
                0.0,
                0,
                0,
                true,
                skincare,
                innisfree
        );
        volcanicMask.addImage(createImage("https://m.media-amazon.com/images/I/71OAKiRewkL._SL1500_.jpg", "inn_vpm_001", "Innisfree Volcanic Mask - Ảnh chính", true, 0));
        volcanicMask.addImage(createImage("https://cdn.shopify.com/s/files/1/0878/2578/files/super-volcanic-pore-clay-mask-desc-8.jpg", "inn_vpm_002", "Innisfree Volcanic Mask - Kết cấu", false, 1));
        volcanicMask.addVariant(createVariant("INN-VPM-100", "Hộp 100ml", new BigDecimal("250000"), new BigDecimal("290000"), 140, 980, "https://skinguru.ro/wp-content/uploads/2022/09/Add-a-subheading-111.jpg"));
        volcanicMask.addVariant(createVariant("INN-VPM-200", "Hộp 200ml (Siêu tiết kiệm)", new BigDecimal("350000"), new BigDecimal("420000"), 70, 580, "https://lilabeauty.com.au/cdn/shop/products/buy-innisfree-super-volcanic-pore-clay-mask-100ml-at-lila-beauty-korean-and-japanese-beauty-skin-care-677321_800x.jpg?v=1693629628"));
        productRepository.save(volcanicMask);

        // --- SẢN PHẨM 6: CeraVe Moisturizing Cream ---
        Product ceraveCream = createProduct(
                "CeraVe Moisturizing Cream",
                "cerave-moisturizing-cream",
                "Kem dưỡng ẩm chuyên sâu cho da khô đến rất khô. Chứa 3 Ceramides thiết yếu và MVE Technology giúp giữ ẩm 24h. Không gây bít tắc lỗ chân lông.",
                "Purified Water, Glycerin, Cetearyl Alcohol, Caprylic/Capric Triglyceride, Ceramide 1, Ceramide 3, Ceramide 6-II, Hyaluronic Acid, Cholesterol",
                "Thoa đều lên da mặt và cơ thể sau khi làm sạch. Dùng sáng và tối, hoặc khi cần thiết.",
                List.of("Da khô", "Da thường", "Da nhạy cảm"),
                List.of("Da khô", "Nứt nẻ", "Thiếu ẩm"),
                new BigDecimal("280000"),
                new BigDecimal("650000"),
                0.0,
                0,
                0,
                true,
                moisturizer,
                cerave
        );
        ceraveCream.addImage(createImage("https://images.unsplash.com/photo-1617897903246-719242758050?w=600", "cer_mc_001", "CeraVe Moisturizing Cream - Ảnh chính", true, 0));
        ceraveCream.addImage(createImage("https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600", "cer_mc_002", "CeraVe Moisturizing Cream - Chi tiết", false, 1));
        ceraveCream.addImage(createImage("https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600", "cer_mc_003", "CeraVe Moisturizing Cream - Kết cấu", false, 2));
        ceraveCream.addVariant(createVariant("CER-MC-177", "Hũ 177ml", new BigDecimal("280000"), new BigDecimal("340000"), 160, 1800, "https://images.unsplash.com/photo-1617897903246-719242758050?w=600"));
        ceraveCream.addVariant(createVariant("CER-MC-340", "Hũ 340ml (Giá tốt)", new BigDecimal("450000"), new BigDecimal("550000"), 110, 980, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600"));
        ceraveCream.addVariant(createVariant("CER-MC-539", "Hũ 539ml (Size gia đình)", new BigDecimal("650000"), new BigDecimal("780000"), 50, 420, "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600"));
        productRepository.save(ceraveCream);

        // --- SẢN PHẨM 7: The Ordinary AHA 30% + BHA 2% Peeling Solution ---
        Product peelingSolution = createProduct(
                "The Ordinary AHA 30% + BHA 2% Peeling Solution",
                "the-ordinary-aha-30-bha-2-peeling-solution",
                "Mặt nạ tẩy tế bào chết hóa học cao cấp với 30% AHA và 2% BHA. Làm sáng da, mờ thâm nám, cải thiện kết cấu da. Chỉ dùng tối đa 2 lần/tuần.",
                "Aqua, Glycolic Acid, Aloe Barbadensis Leaf Water, Sodium Hydroxide, Daucus Carota Sativa Extract, Propanediol, Cocamidopropyl Dimethylamine, Salicylic Acid",
                "Dùng tối, thoa đều lên da sạch, tránh vùng mắt. Để 10 phút KHÔNG QUÁ. Rửa sạch. Dùng tối đa 2 lần/tuần. BẮT BUỘC dùng kem chống nắng vào ngày hôm sau.",
                List.of("Da thường", "Da dầu", "Da hỗn hợp"),
                List.of("Thâm nám", "Sạm da", "Lỗ chân lông to", "Mụn"),
                new BigDecimal("230000"),
                new BigDecimal("230000"),
                0.0,
                0,
                0,
                false,
                serum,
                theOrdinary
        );
        peelingSolution.addImage(createImage("https://images.unsplash.com/photo-1570554886111-e80fcca6a029?w=600", "ord_aha_001", "The Ordinary AHA BHA - Ảnh chính", true, 0));
        peelingSolution.addImage(createImage("https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600", "ord_aha_002", "The Ordinary AHA BHA - Cảnh báo", false, 1));
        peelingSolution.addVariant(createVariant("ORD-AHA-30", "Chai 30ml", new BigDecimal("230000"), new BigDecimal("280000"), 100, 400, "https://images.unsplash.com/photo-1570554886111-e80fcca6a029?w=600"));
        productRepository.save(peelingSolution);

        // --- SẢN PHẨM 8: Innisfree Jeju Orchid Enriched Cream ---
        Product orchidCream = createProduct(
                "Innisfree Jeju Orchid Enriched Cream",
                "innisfree-jeju-orchid-enriched-cream",
                "Kem dưỡng chống lão hóa cao cấp với chiết xuất hoa lan Jeju. Giúp săn chắc, nâng cơ, cải thiện độ đàn hồi và giảm nếp nhăn.",
                "Water, Glycerin, Orchid Extract (5,000ppm), Niacinamide, Adenosine, Ceramide NP, Peptide Complex, Tocopherol, Squalane",
                "Lấy 1 lượng vừa đủ, chấm 5 điểm lên mặt (trán, má, mũi, cằm). Massage nhẹ nhàng theo chiều từ trong ra ngoài, từ dưới lên trên. Dùng sáng và tối.",
                List.of("Mọi loại da", "Da lão hóa", "Da mất độ đàn hồi"),
                List.of("Lão hóa", "Nếp nhăn", "Chảy xệ", "Thiếu độ đàn hồi"),
                new BigDecimal("650000"),
                new BigDecimal("850000"),
                0.0,
                0,
                0,
                true,
                moisturizer,
                innisfree
        );
        orchidCream.addImage(createImage("https://images.unsplash.com/photo-1617897903246-719242758050?w=600", "inn_oec_001", "Innisfree Orchid Cream - Ảnh chính", true, 0));
        orchidCream.addImage(createImage("https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=600", "inn_oec_002", "Innisfree Orchid Cream - Sang trọng", false, 1));
        orchidCream.addImage(createImage("https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=600", "inn_oec_003", "Innisfree Orchid Cream - Kết cấu", false, 2));
        orchidCream.addVariant(createVariant("INN-OEC-50", "Hũ 50ml", new BigDecimal("650000"), new BigDecimal("750000"), 80, 400, "https://images.unsplash.com/photo-1617897903246-719242758050?w=600"));
        orchidCream.addVariant(createVariant("INN-OEC-100", "Hũ 100ml (Dùng lâu)", new BigDecimal("850000"), new BigDecimal("1050000"), 40, 167, "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=600"));
        productRepository.save(orchidCream);

        // --- SẢN PHẨM 9: The Ordinary Natural Moisturizing Factors + HA ---
        Product nmfHA = createProduct(
                "The Ordinary Natural Moisturizing Factors + HA",
                "the-ordinary-nmf-ha",
                "Kem dưỡng ẩm cơ bản với các yếu tố dưỡng ẩm tự nhiên và Hyaluronic Acid. Công thức nhẹ, không gây nhờn, phù hợp làm lớp nền trang điểm.",
                "Aqua, Caprylic/Capric Triglyceride, Cetyl Alcohol, Propanediol, Glycerin, Sodium Hyaluronate, Amino Acids, Fatty Acids, Triglycerides, Urea",
                "Thoa đều lên da sau các bước serum. Dùng sáng và tối. Có thể dùng làm primer trước khi makeup.",
                List.of("Mọi loại da", "Da thường", "Da hỗn hợp"),
                List.of("Thiếu ẩm", "Da khô"),
                new BigDecimal("150000"),
                new BigDecimal("290000"),
                0.0,
                0,
                0,
                true,
                moisturizer,
                theOrdinary
        );
        nmfHA.addImage(createImage("https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=600", "ord_nmf_001", "The Ordinary NMF - Ảnh chính", true, 0));
        nmfHA.addImage(createImage("https://images.unsplash.com/photo-1571875257727-256c39da42af?w=600", "ord_nmf_002", "The Ordinary NMF - Chi tiết", false, 1));
        nmfHA.addVariant(createVariant("ORD-NMF-30", "Tuýp 30ml", new BigDecimal("150000"), new BigDecimal("180000"), 200, 900, "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=600"));
        nmfHA.addVariant(createVariant("ORD-NMF-100", "Tuýp 100ml (Tiết kiệm)", new BigDecimal("290000"), new BigDecimal("350000"), 120, 440, "https://images.unsplash.com/photo-1571875257727-256c39da42af?w=600"));
        productRepository.save(nmfHA);

        // --- SẢN PHẨM 10: CeraVe Foaming Facial Cleanser ---
        Product foamingCleanser = createProduct(
                "CeraVe Foaming Facial Cleanser",
                "cerave-foaming-facial-cleanser",
                "Sữa rửa mặt dạng gel tạo bọt cho da thường đến da dầu. Làm sạch dầu thừa và bụi bẩn mà không làm khô da. Chứa Niacinamide và Ceramides.",
                "Purified Water, Cocamidopropyl Hydroxysultaine, Glycerin, Sodium Lauroyl Sarcosinate, Niacinamide, Ceramide 1, Ceramide 3, Ceramide 6-II, Hyaluronic Acid",
                "Làm ướt mặt, cho 1-2 pump gel lên tay, tạo bọt và massage nhẹ nhàng. Rửa sạch với nước. Dùng sáng và tối.",
                List.of("Da thường", "Da dầu", "Da hỗn hợp"),
                List.of("Dầu nhờn", "Mụn", "Làm sạch sâu"),
                new BigDecimal("220000"),
                new BigDecimal("360000"),
                0.0,
                0,
                0,
                true,
                cleanser,
                cerave
        );
        foamingCleanser.addImage(createImage("https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=600", "cer_ffc_001", "CeraVe Foaming Cleanser - Ảnh chính", true, 0));
        foamingCleanser.addImage(createImage("https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600", "cer_ffc_002", "CeraVe Foaming Cleanser - Bọt", false, 1));
        foamingCleanser.addVariant(createVariant("CER-FFC-236", "Chai 236ml", new BigDecimal("220000"), new BigDecimal("270000"), 180, 1300, "https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=600"));
        foamingCleanser.addVariant(createVariant("CER-FFC-355", "Chai 355ml (Tiết kiệm)", new BigDecimal("360000"), new BigDecimal("430000"), 95, 830, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600"));
        productRepository.save(foamingCleanser);

        // --- SẢN PHẨM 11: La Roche-Posay Effaclar Duo+M ---
        Product effaclarDuo = createProduct(
                "La Roche-Posay Effaclar Duo+M",
                "la-roche-posay-effaclar-duo-m",
                "Kem gel giảm mụn và hỗ trợ làm giảm thâm sau mụn, phù hợp cho da dầu và da mụn.",
                "Aqua, Glycerin, Niacinamide, LHA, Salicylic Acid, Procerad, Zinc PCA",
                "Thoa một lượng mỏng lên vùng da mụn sau bước toner, dùng sáng và tối.",
                List.of("Da dầu", "Da mụn", "Da hỗn hợp"),
                List.of("Mụn", "Thâm sau mụn", "Dầu nhờn"),
                new BigDecimal("320000"),
                new BigDecimal("420000"),
                0.0,
                0,
                0,
                true,
                serum,
                larochePosay
        );
        effaclarDuo.addImage(createImage("https://www.larocheposay.vn/-/media/project/loreal/brand-sites/lrp/apac/vn/products/cicaplast/cicaplast-baume-b5-plus/1-new.png", "lrp_ed_001", "Effaclar Duo+M - Ảnh chính", true, 0));
        effaclarDuo.addImage(createImage("https://www.larocheposay.vn/-/media/project/loreal/brand-sites/lrp/apac/vn/products/cicaplast/cicaplast-baume-b5-plus/5.jpg", "lrp_ed_002", "Effaclar Duo+M - Ảnh cận cảnh", false, 1));
        effaclarDuo.addImage(createImage("https://www.larocheposay.vn/-/media/project/loreal/brand-sites/lrp/apac/vn/products/cicaplast/cicaplast-baume-b5-plus/2.jpg", "lrp_ed_003", "Effaclar Duo+M - Ảnh texture", false, 2));
        effaclarDuo.addVariant(createVariant("LRP-ED-40", "Tuýp 40ml", new BigDecimal("320000"), new BigDecimal("390000"), 140, 620, "https://www.larocheposay.vn/-/media/project/loreal/brand-sites/lrp/apac/vn/products/cicaplast/cicaplast-baume-b5-plus/1-new.png"));
        effaclarDuo.addVariant(createVariant("LRP-ED-60", "Tuýp 60ml", new BigDecimal("420000"), new BigDecimal("520000"), 90, 360, "https://www.larocheposay.vn/-/media/project/loreal/brand-sites/lrp/apac/vn/products/cicaplast/gel-b5-pro-recovery/damaged/la-roche-posay-productpage-damaged-cicaplast-gel-b5-pro-recovery-40ml-3337875586269-front.png?sc_lang=vi-vn"));
        productRepository.save(effaclarDuo);

        // --- SẢN PHẨM 12: COSRX Advanced Snail 96 Mucin Power Essence ---
        Product snailEssence = createProduct(
                "COSRX Advanced Snail 96 Mucin Power Essence",
                "cosrx-advanced-snail-96-mucin-power-essence",
                "Tinh chất phục hồi với 96% dịch nhầy ốc sên giúp cấp ẩm, làm dịu và hỗ trợ cải thiện bề mặt da.",
                "Snail Secretion Filtrate 96%, Betaine, Sodium Hyaluronate, Panthenol, Arginine",
                "Sau toner, lấy 1-2 pump tinh chất, thoa đều lên toàn mặt và vỗ nhẹ cho thấm.",
                List.of("Mọi loại da", "Da khô", "Da nhạy cảm"),
                List.of("Thiếu ẩm", "Da yếu", "Sau mụn"),
                new BigDecimal("290000"),
                new BigDecimal("390000"),
                0.0,
                0,
                0,
                true,
                serum,
                cosrx
        );
        snailEssence.addImage(createImage("https://images.unsplash.com/photo-1612817288484-6f916006741a?w=600", "cos_snail_001", "Snail Essence - Ảnh chính", true, 0));
        snailEssence.addImage(createImage("https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=600", "cos_snail_002", "Snail Essence - Ảnh sản phẩm", false, 1));
        snailEssence.addImage(createImage("https://images.unsplash.com/photo-1602910344008-22f323cc1818?w=600", "cos_snail_003", "Snail Essence - Ảnh chi tiết", false, 2));
        snailEssence.addVariant(createVariant("COS-SN-100", "Chai 100ml", new BigDecimal("290000"), new BigDecimal("350000"), 160, 900, "https://images.unsplash.com/photo-1612817288484-6f916006741a?w=600"));
        snailEssence.addVariant(createVariant("COS-SN-120", "Chai 120ml", new BigDecimal("390000"), new BigDecimal("460000"), 100, 580, "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=600"));
        productRepository.save(snailEssence);

        // --- SẢN PHẨM 13: La Roche-Posay Anthelios UVMune 400 ---
        Product anthelios = createProduct(
                "La Roche-Posay Anthelios UVMune 400 Invisible Fluid",
                "la-roche-posay-anthelios-uvmune-400-invisible-fluid",
                "Kem chống nắng phổ rộng, kết cấu mỏng nhẹ, hỗ trợ bảo vệ da trước tia UV mạnh và thích hợp dùng hằng ngày.",
                "Aqua, Alcohol Denat., Glycerin, Mexoryl 400, Mexoryl XL, Niacinamide",
                "Lắc đều trước khi dùng, thoa lượng đủ lên da trước khi ra nắng 15-20 phút. Thoa lại sau mỗi 2-3 giờ.",
                List.of("Mọi loại da", "Da dầu", "Da nhạy cảm"),
                List.of("Chống nắng", "Bảo vệ da", "Dùng hằng ngày"),
                new BigDecimal("360000"),
                new BigDecimal("480000"),
                0.0,
                0,
                0,
                true,
                sunscreen,
                larochePosay
        );
        anthelios.addImage(createImage("https://d3pllp7nz3wmw5.cloudfront.net/product_images/27735308101-1_XL.jpg", "lrp_sun_001", "Anthelios - Ảnh chính", true, 0));
        anthelios.addImage(createImage("https://univers-1d470.kxcdn.com/37265-square_large_default/la-roche-posay-la-roche-posay-anthelios-uvmune-400-fluide-anti-taches-spf50-50-ml-soins-eclat-et-anti-taches.jpg", "lrp_sun_002", "Anthelios - Ảnh cận cảnh", false, 1));
        anthelios.addImage(createImage("https://www.facethefuture.co.uk/cdn/shop/files/UVMune400InvisibleFluid.png?v=1692777695&width=600", "lrp_sun_003", "Anthelios - Ảnh texture", false, 2));
        anthelios.addVariant(createVariant("LRP-UV-50", "Chai 50ml", new BigDecimal("360000"), new BigDecimal("420000"), 150, 540, "https://farmaciajimenez.com/storage/products/la-roche-posay-anthelios-uvmune-400-fluido-color-oil-control-spf-50-50-ml/la-roche-posay-anthelios-uv-inmune-400-fluido-color-envase-y-tubo-640.jpg"));
        anthelios.addVariant(createVariant("LRP-UV-100", "Chai 100ml", new BigDecimal("480000"), new BigDecimal("560000"), 70, 320, "https://shop.sunwaymedicalvelocity.com.my/images/uploads/product/109/SVC_la-roche-posay-anthelios-uvmune-400-invisible-fluid-spf-50-50ml.png"));
        productRepository.save(anthelios);

        // --- SẢN PHẨM 14: Vaseline Healthy Bright Daily Lotion ---
        Product vaselineLotion = createProduct(
                "Vaseline Healthy Bright Daily Lotion",
                "vaseline-healthy-bright-daily-lotion",
                "Sữa dưỡng thể giúp cấp ẩm, hỗ trợ làm sáng da và duy trì làn da mềm mịn toàn thân.",
                "Water, Glycerin, Niacinamide, Petrolatum, Vitamin B3, Sunflower Seed Oil",
                "Thoa đều lên cơ thể sau khi tắm, dùng mỗi ngày để duy trì độ ẩm và độ sáng cho da.",
                List.of("Mọi loại da", "Da khô", "Da thường"),
                List.of("Dưỡng thể", "Làm mềm da", "Làm sáng da"),
                new BigDecimal("130000"),
                new BigDecimal("240000"),
                0.0,
                0,
                0,
                false,
                bodyCare,
                cerave
        );
        vaselineLotion.addImage(createImage("https://assets.unileversolutions.com/v1/1627856.png", "vas_body_001", "Vaseline Lotion - Ảnh chính", true, 0));
        vaselineLotion.addImage(createImage("https://cdn01.pharmeasy.in/dam/products_otc/P28581/vaseline-healthy-bright-daily-brightening-body-lotion-200-ml-6.1-1643286539.jpg", "vas_body_002", "Vaseline Lotion - Ảnh sản phẩm", false, 1));
        vaselineLotion.addVariant(createVariant("VAS-BR-320", "Chai 320ml", new BigDecimal("130000"), new BigDecimal("170000"), 220, 980, "https://cdn01.pharmeasy.in/dam/products_otc/P28581/vaseline-healthy-bright-daily-brightening-body-lotion-200-ml-6.1-1643286539.jpg"));
        vaselineLotion.addVariant(createVariant("VAS-BR-725", "Chai 725ml (Family Size)", new BigDecimal("240000"), new BigDecimal("290000"), 130, 620, "https://www.distacart.com/cdn/shop/files/Add_a_heading_36_1280x.png?v=1737607461"));
        productRepository.save(vaselineLotion);

        log.info("Da tao thanh cong {} san pham gom variants tuong ung!", 14);
        log.info("Nap du lieu thanh cong");
    }

    // ========== Helper Methods ==========

    private Product createProduct(String name, String slug, String description, String ingredients,
                                   String usageInstructions, List<String> suitableSkinTypes,
                                   List<String> skinConcerns, BigDecimal minPrice, BigDecimal maxPrice,
                                   Double averageRating, Integer totalReviews, Integer totalSold,
                                   Boolean isFeatured, Category category, Brand brand) {
        
        // Chuẩn hóa và lọc danh sách loại da từ Tiếng Việt thành chuỗi slug
        List<String> formattedSkinTypes = suitableSkinTypes == null ? List.of() : suitableSkinTypes.stream()
                .map(SkinTypeSlugUtils::toSlug)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        return Product.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .ingredients(ingredients)
                .usageInstructions(usageInstructions)
                .suitableSkinTypes(formattedSkinTypes)
                .skinConcerns(skinConcerns)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .totalSold(totalSold)
                .isFeatured(isFeatured)
                .isActive(true)
                .category(category)
                .brand(brand)
                .build();
    }

    private ProductImage createImage(String url, String publicId, String altText, Boolean isPrimary, Integer displayOrder) {
        return ProductImage.builder()
                .url(url)
                .publicId(publicId)
                .altText(altText)
                .isPrimary(isPrimary)
                .displayOrder(displayOrder)
                .build();
    }

    private ProductVariant createVariant(String sku, String variantName, BigDecimal price,
                                          BigDecimal originalPrice, Integer stockQuantity,
                                          Integer sold, String imageUrl) {
        return ProductVariant.builder()
                .sku(sku)
                .variantName(variantName)
                .price(price)
                .originalPrice(originalPrice)
                .stockQuantity(stockQuantity)
                .sold(sold)
                .imageUrl(imageUrl)
                .isActive(true)
                .build();
    }
}

