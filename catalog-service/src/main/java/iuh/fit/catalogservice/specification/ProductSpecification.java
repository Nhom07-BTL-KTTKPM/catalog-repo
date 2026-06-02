package iuh.fit.catalogservice.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import iuh.fit.catalogservice.dto.request.ProductFilterRequest;
import iuh.fit.catalogservice.entity.Product;
import iuh.fit.catalogservice.util.SkinTypeSlugUtils;
import iuh.fit.catalogservice.util.SlugGenerator;
import jakarta.persistence.criteria.Predicate;

/**
 * Dynamic specification builder for product filtering.
 */
public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> byFilter(ProductFilterRequest filterRequest) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            if (filterRequest == null) {
                return combineAnd(predicates, cb);
            }

            addKeywordPredicate(filterRequest.getKeyword(), root, cb, predicates);
            addCategoryPredicate(filterRequest.getCategoryIds(), root, predicates);
            addBrandPredicate(filterRequest.getBrandIds(), root, predicates);
            addSkinTypePredicate(filterRequest.getSkinTypes(), root, cb, predicates);
            addPricePredicate(filterRequest.getMinPrice(), filterRequest.getMaxPrice(), root, cb, predicates);
            addRatingPredicate(filterRequest.getRating(), root, cb, predicates);

            return combineAnd(predicates, cb);
        };
    }

    private static void addKeywordPredicate(String keyword,
            jakarta.persistence.criteria.Root<Product> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            List<Predicate> predicates) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String normalizedKeyword = keyword.trim().toLowerCase();
        String textPattern = "%" + normalizedKeyword + "%";
        String slugPattern = "%" + SlugGenerator.generate(keyword.trim()).toLowerCase() + "%";
        predicates.add(cb.or(
            cb.like(cb.lower(root.get("name")), textPattern),
            cb.like(cb.lower(root.get("description")), textPattern),
            cb.like(cb.lower(root.get("slug")), slugPattern)));
    }

    private static void addCategoryPredicate(List<java.util.UUID> categoryIds,
            jakarta.persistence.criteria.Root<Product> root,
            List<Predicate> predicates) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        predicates.add(root.get("category").get("id").in(categoryIds));
    }

    private static void addBrandPredicate(List<java.util.UUID> brandIds,
            jakarta.persistence.criteria.Root<Product> root,
            List<Predicate> predicates) {
        if (brandIds == null || brandIds.isEmpty()) {
            return;
        }

        predicates.add(root.get("brand").get("id").in(brandIds));
    }

    private static void addSkinTypePredicate(List<String> skinTypes,
            jakarta.persistence.criteria.Root<Product> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            List<Predicate> predicates) {
        if (skinTypes == null || skinTypes.isEmpty()) {
            return;
        }

        jakarta.persistence.criteria.Expression<String> storedSkinTypes = root.get("suitableSkinTypes").as(String.class);
        List<Predicate> skinTypePredicates = new ArrayList<>();

        for (String skinType : skinTypes) {
            String slug = SkinTypeSlugUtils.toSlug(skinType);
            if (slug.isEmpty()) {
                continue;
            }

            skinTypePredicates.add(cb.like(cb.lower(storedSkinTypes), "%" + slug.toLowerCase() + "%"));
        }

        if (!skinTypePredicates.isEmpty()) {
            predicates.add(combineOr(skinTypePredicates, cb));
        }
    }

    private static void addPricePredicate(BigDecimal minPrice,
            BigDecimal maxPrice,
            jakarta.persistence.criteria.Root<Product> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            List<Predicate> predicates) {
        if (minPrice == null && maxPrice == null) {
            return;
        }

        if (minPrice != null && maxPrice != null) {
            predicates.add(cb.and(
                    cb.lessThanOrEqualTo(root.get("minPrice"), maxPrice),
                    cb.greaterThanOrEqualTo(root.get("maxPrice"), minPrice)));
            return;
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("maxPrice"), minPrice));
            return;
        }

        predicates.add(cb.lessThanOrEqualTo(root.get("minPrice"), maxPrice));
    }

    private static void addRatingPredicate(Integer rating,
            jakarta.persistence.criteria.Root<Product> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            List<Predicate> predicates) {
        if (rating == null) {
            return;
        }

        predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), rating.doubleValue()));
    }

    private static Predicate combineAnd(List<Predicate> predicates, jakarta.persistence.criteria.CriteriaBuilder cb) {
        if (predicates.isEmpty()) {
            return cb.conjunction();
        }

        Predicate combined = predicates.get(0);
        for (int index = 1; index < predicates.size(); index++) {
            combined = cb.and(combined, predicates.get(index));
        }
        return combined;
    }

    private static Predicate combineOr(List<Predicate> predicates, jakarta.persistence.criteria.CriteriaBuilder cb) {
        if (predicates.isEmpty()) {
            return cb.disjunction();
        }

        Predicate combined = predicates.get(0);
        for (int index = 1; index < predicates.size(); index++) {
            combined = cb.or(combined, predicates.get(index));
        }
        return combined;
    }
}