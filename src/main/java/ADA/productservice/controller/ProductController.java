package ADA.productservice.controller;

import ADA.productservice.dto.CreateProductRequest;
import ADA.productservice.dto.ProductDetailDto;
import ADA.productservice.dto.ProductSummaryDto;
import ADA.productservice.service.ProductService;
import ADA.productservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;
    private final UserService userService;

    @GetMapping
    public List<ProductSummaryDto> getProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) List<Integer> validAttributeIds,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) Integer recommendForUserId,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "true") Boolean inStock) {
        return service.getProducts(categoryId, validAttributeIds, priceMin, priceMax, recommendForUserId, searchQuery, inStock);
    }

    @GetMapping("/{id}")
    public ProductDetailDto getById(@PathVariable Integer id, Authentication auth) {
        if (auth != null) {
            userService.addToHistory(extractUserId(auth), id);
        }
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetailDto create(@Valid @RequestBody CreateProductRequest request, Authentication auth) {
        return service.create(request, extractUserId(auth));
    }

    @PutMapping("/{id}")
    public ProductDetailDto update(@PathVariable Integer id,
                                   @Valid @RequestBody CreateProductRequest request,
                                   Authentication auth) {
        return service.update(id, request, extractUserId(auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id, Authentication auth) {
        service.delete(id, extractUserId(auth));
    }

    private Integer extractUserId(Authentication auth) {
        return Integer.parseInt((String) auth.getPrincipal());
    }
}
