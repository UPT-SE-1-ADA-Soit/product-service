package ADA.productservice.controller;

import ADA.productservice.dto.ProductSummaryDto;
import ADA.productservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/{userId}/listed-products")
    public List<ProductSummaryDto> getListedProducts(@PathVariable Integer userId) {
        return service.getListedProducts(userId);
    }

    @PostMapping("/{userId}/listed-products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToListedProducts(@PathVariable Integer userId,
                                    @PathVariable Integer productId,
                                    Authentication auth) {
        service.addToListedProducts(userId, productId, extractUserId(auth));
    }

    @DeleteMapping("/{userId}/listed-products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromListedProducts(@PathVariable Integer userId,
                                         @PathVariable Integer productId,
                                         Authentication auth) {
        service.removeFromListedProducts(userId, productId, extractUserId(auth));
    }

    @GetMapping("/{userId}/history")
    public List<ProductSummaryDto> getHistory(@PathVariable Integer userId) {
        return service.getHistory(userId);
    }

    @PostMapping("/{userId}/history/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToHistory(@PathVariable Integer userId, @PathVariable Integer productId) {
        service.addToHistory(userId, productId);
    }

    @GetMapping("/{userId}/favorites")
    public List<ProductSummaryDto> getFavorites(@PathVariable Integer userId) {
        return service.getFavorites(userId);
    }

    @PostMapping("/{userId}/favorites/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addFavorite(@PathVariable Integer userId,
                            @PathVariable Integer productId,
                            Authentication auth) {
        service.addFavorite(userId, productId, extractUserId(auth));
    }

    @DeleteMapping("/{userId}/favorites/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(@PathVariable Integer userId,
                               @PathVariable Integer productId,
                               Authentication auth) {
        service.removeFavorite(userId, productId, extractUserId(auth));
    }

    private Integer extractUserId(Authentication auth) {
        return Integer.parseInt((String) auth.getPrincipal());
    }
}
