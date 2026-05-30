package ADA.productservice.service;

import ADA.productservice.dto.OrderDto;
import ADA.productservice.entity.Order;
import ADA.productservice.entity.Product;
import ADA.productservice.entity.User;
import ADA.productservice.exception.ConflictException;
import ADA.productservice.exception.ForbiddenException;
import ADA.productservice.exception.ResourceNotFoundException;
import ADA.productservice.repository.OrderRepository;
import ADA.productservice.repository.ProductRepository;
import ADA.productservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersForUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return orderRepository.findAllByUser_Id(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public OrderDto createOrder(Integer userId, Integer productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (!product.isInStock()) {
            throw new ConflictException("Product is not available");
        }

        product.setInStock(false);
        productRepository.save(product);

        return toDto(orderRepository.save(Order.builder()
                .user(user)
                .product(product)
                .build()));
    }

    @Transactional
    public void cancelOrder(Integer orderId, Integer requesterId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (!order.getUser().getId().equals(requesterId)) {
            throw new ForbiddenException("You can only cancel your own orders");
        }

        order.getProduct().setInStock(true);
        productRepository.save(order.getProduct());
        orderRepository.deleteById(orderId);
    }

    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getName())
                .orderedAt(order.getOrderedAt())
                .build();
    }
}
