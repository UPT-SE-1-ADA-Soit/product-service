package ADA.productservice.service;

import ADA.productservice.dto.*;
import ADA.productservice.entity.*;
import ADA.productservice.exception.ForbiddenException;
import ADA.productservice.exception.ResourceNotFoundException;
import ADA.productservice.repository.*;
import ADA.productservice.spec.ProductSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AttributeValuePairRepository pairRepository;
    private final CategoryRepository categoryRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final HistoryRepository historyRepository;
    private final ListedProductRepository listedProductRepository;
    private final UserRepository userRepository;
    private final RecommendationService recommendationService;

    @Transactional(readOnly = true)
    public List<ProductSummaryDto> getProducts(
            Integer categoryId, List<Integer> validAttributeIds,
            BigDecimal priceMin, BigDecimal priceMax,
            Integer recommendForUserId, String searchQuery) {

        if (recommendForUserId != null) {
            List<Integer> recommendedIds = recommendationService.getRecommendedIds(recommendForUserId);
            if (!recommendedIds.isEmpty()) {
                Map<Integer, Product> productMap = productRepository.findAllById(recommendedIds)
                        .stream()
                        .collect(Collectors.toMap(Product::getId, p -> p));
                return recommendedIds.stream()
                        .filter(productMap::containsKey)
                        .map(id -> toSummaryDto(productMap.get(id)))
                        .toList();
            }
        }

        Specification<Product> spec = ProductSpec.withFilters(
                categoryId, priceMin, priceMax, searchQuery, null);

        if (validAttributeIds != null && !validAttributeIds.isEmpty()) {
            spec = spec.and(ProductSpec.hasAllAttributes(validAttributeIds));
        }

        return productRepository.findAll(spec).stream()
                .map(this::toSummaryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDetailDto getById(Integer id) {
        return toDetailDto(findProductById(id));
    }

    @Transactional
    public ProductDetailDto create(CreateProductRequest request, Integer sellerId) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", sellerId));

        Product product = productRepository.save(Product.builder()
                .name(request.getName())
                .category(category)
                .price(request.getPrice())
                .description(request.getDescription())
                .region(request.getRegion())
                .build());

        listedProductRepository.save(ListedProduct.builder()
                .user(seller)
                .product(product)
                .build());

        saveAttributePairs(product, request.getAttributes());
        return toDetailDto(product);
    }

    @Transactional
    public ProductDetailDto update(Integer id, CreateProductRequest request, Integer userId) {
        verifyOwnership(userId, id);

        Product product = findProductById(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        product.setName(request.getName());
        product.setCategory(category);
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setRegion(request.getRegion());
        product = productRepository.save(product);

        pairRepository.deleteAllByProduct_Id(id);
        saveAttributePairs(product, request.getAttributes());
        return toDetailDto(product);
    }

    @Transactional
    public void delete(Integer id, Integer userId) {
        verifyOwnership(userId, id);
        pairRepository.deleteAllByProduct_Id(id);
        listedProductRepository.deleteAllByProduct_Id(id);
        historyRepository.deleteAllByProduct_Id(id);
        productRepository.deleteById(id);
    }

    public ProductSummaryDto toSummaryDto(Product product) {
        List<Integer> attrIds = pairRepository.findAllByProduct_Id(product.getId())
                .stream()
                .map(p -> p.getAttribute().getId())
                .distinct()
                .toList();
        return ProductSummaryDto.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategory().getId())
                .price(product.getPrice())
                .attributeIds(attrIds)
                .build();
    }

    private void verifyOwnership(Integer userId, Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }
        if (!listedProductRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw new ForbiddenException("You do not own this listing");
        }
    }

    private ProductDetailDto toDetailDto(Product product) {
        List<ProductAttributeEntryDto> attrs = pairRepository.findAllByProduct_Id(product.getId())
                .stream()
                .map(p -> ProductAttributeEntryDto.builder()
                        .attributeId(p.getAttribute().getId())
                        .attributeName(p.getAttribute().getName())
                        .valueId(p.getAttributeValue().getId())
                        .valueName(p.getAttributeValue().getName())
                        .build())
                .toList();
        return ProductDetailDto.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .addedDate(product.getAddedDate())
                .region(product.getRegion())
                .attributes(attrs)
                .build();
    }

    private void saveAttributePairs(Product product, List<AttributePairRequest> pairs) {
        if (pairs == null || pairs.isEmpty()) return;
        for (AttributePairRequest pair : pairs) {
            Attribute attribute = attributeRepository.findById(pair.getAttributeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Attribute", pair.getAttributeId()));
            AttributeValue value = attributeValueRepository.findById(pair.getAttributeValueId())
                    .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", pair.getAttributeValueId()));
            pairRepository.save(AttributeValuePair.builder()
                    .product(product)
                    .attribute(attribute)
                    .attributeValue(value)
                    .build());
        }
    }

    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
