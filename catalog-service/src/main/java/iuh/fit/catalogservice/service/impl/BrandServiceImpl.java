package iuh.fit.catalogservice.service.impl;

import iuh.fit.catalogservice.dto.request.BrandRequest;
import iuh.fit.catalogservice.dto.response.BrandResponse;
import iuh.fit.catalogservice.dto.response.BrandSummaryResponse;
import iuh.fit.catalogservice.entity.Brand;
import iuh.fit.catalogservice.repo.BrandRepository;
import iuh.fit.catalogservice.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of BrandService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public BrandResponse createBrand(BrandRequest request) {
        log.info("Creating new brand with slug: {}", request.getSlug());

        if (brandRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Brand with slug '" + request.getSlug() + "' already exists");
        }

        Brand brand = Brand.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .originCountry(request.getOriginCountry())
                .websiteUrl(request.getWebsiteUrl())
                .isActive(request.getIsActive())
                .build();

        Brand savedBrand = brandRepository.save(brand);
        log.info("Created brand with ID: {}", savedBrand.getId());

        return mapToResponse(savedBrand);
    }

    @Override
    public BrandResponse updateBrand(UUID id, BrandRequest request) {
        log.info("Updating brand with ID: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found with ID: " + id));

        if (!brand.getSlug().equals(request.getSlug()) &&
            brandRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Brand with slug '" + request.getSlug() + "' already exists");
        }

        brand.setName(request.getName());
        brand.setSlug(request.getSlug());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setOriginCountry(request.getOriginCountry());
        brand.setWebsiteUrl(request.getWebsiteUrl());
        brand.setIsActive(request.getIsActive());

        Brand updatedBrand = brandRepository.save(brand);
        log.info("Updated brand with ID: {}", updatedBrand.getId());

        return mapToResponse(updatedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(UUID id) {
        log.debug("Fetching brand with ID: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found with ID: " + id));

        return mapToResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandBySlug(String slug) {
        log.debug("Fetching brand with slug: {}", slug);

        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found with slug: " + slug));

        return mapToResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandResponse> getAllBrands(Pageable pageable) {
        log.debug("Fetching all brands with pagination");

        return brandRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getActiveBrands() {
        log.debug("Fetching all active brands");

        return brandRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBrand(UUID id) {
        log.info("Deleting brand with ID: {}", id);

        if (!brandRepository.existsById(id)) {
            throw new IllegalArgumentException("Brand not found with ID: " + id);
        }

        brandRepository.deleteById(id);
        log.info("Deleted brand with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> searchBrands(String keyword) {
        log.debug("Searching brands with keyword: {}", keyword);

        return brandRepository.searchByName(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getBrandsByOriginCountry(String originCountry) {
        log.debug("Fetching brands from origin country: {}", originCountry);

        return brandRepository.findByOriginCountryAndIsActiveTrue(originCountry)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandSummaryResponse> getActiveBrandSummaries() {
        log.debug("Fetching active brand summaries");
        return brandRepository.findAllActiveSummaries();
    }

    private BrandResponse mapToResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .originCountry(brand.getOriginCountry())
                .websiteUrl(brand.getWebsiteUrl())
                .isActive(brand.getIsActive())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}

