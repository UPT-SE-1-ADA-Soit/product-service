package ADA.productservice.controller;

import ADA.productservice.dto.OrderDto;
import ADA.productservice.dto.OrderRequest;
import ADA.productservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@Valid @RequestBody OrderRequest request, Authentication auth) {
        return orderService.createOrder(extractUserId(auth), request.getProductId());
    }

    @GetMapping("/user/{userId}/orders")
    public List<OrderDto> getOrdersForUser(@PathVariable Integer userId) {
        return orderService.getOrdersForUser(userId);
    }

    @DeleteMapping("/order/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Integer orderId, Authentication auth) {
        orderService.cancelOrder(orderId, extractUserId(auth));
    }

    private Integer extractUserId(Authentication auth) {
        return Integer.parseInt((String) auth.getPrincipal());
    }
}
