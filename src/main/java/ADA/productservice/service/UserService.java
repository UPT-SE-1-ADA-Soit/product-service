package ADA.productservice.service;

import ADA.productservice.dto.ProductSummaryDto;
import ADA.productservice.entity.History;
import ADA.productservice.entity.ListedProduct;
import ADA.productservice.entity.Product;
import ADA.productservice.entity.User;
import ADA.productservice.exception.ForbiddenException;
import ADA.productservice.exception.ResourceNotFoundException;
import ADA.productservice.repository.HistoryRepository;
import ADA.productservice.repository.ListedProductRepository;
import ADA.productservice.repository.ProductRepository;
import ADA.productservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ListedProductRepository listedProductRepository;
    private final HistoryRepository historyRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<ProductSummaryDto> getListedProducts(Integer userId) {
        verifyUserExists(userId);
        return listedProductRepository.findAllByUser_Id(userId).stream()
                .map(lp -> productService.toSummaryDto(lp.getProduct()))
                .toList();
    }

    @Transactional
    public void addToListedProducts(Integer userId, Integer productId, Integer requesterId) {
        verifyIsSelf(userId, requesterId);
        if (listedProductRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            return;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        listedProductRepository.save(ListedProduct.builder().user(user).product(product).build());
    }

    @Transactional
    public void removeFromListedProducts(Integer userId, Integer productId, Integer requesterId) {
        verifyIsSelf(userId, requesterId);
        verifyUserExists(userId);
        listedProductRepository.deleteByUser_IdAndProduct_Id(userId, productId);
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryDto> getHistory(Integer userId) {
        verifyUserExists(userId);
        return historyRepository.findAllByUser_IdOrderByIdAsc(userId).stream()
                .map(h -> productService.toSummaryDto(h.getProduct()))
                .toList();
    }

    @Transactional
    public void addToHistory(Integer userId, Integer productId) {
        if (listedProductRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            return;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        // Delete existing entry so it gets re-inserted with a new ID (moves to last)
        historyRepository.deleteByUser_IdAndProduct_Id(userId, productId);
        historyRepository.save(History.builder().user(user).product(product).build());
    }

    private void verifyUserExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
    }

    private void verifyIsSelf(Integer userId, Integer requesterId) {
        if (!userId.equals(requesterId)) {
            throw new ForbiddenException("You can only manage your own listings");
        }
    }
}
