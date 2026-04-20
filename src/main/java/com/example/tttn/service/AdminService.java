package com.example.tttn.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tttn.model.Category;
import com.example.tttn.model.Order;
import com.example.tttn.model.Product;
import com.example.tttn.repository.CategoryRepository;
import com.example.tttn.repository.OrderRepository;
import com.example.tttn.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;

    public AdminService(ProductRepository productRepository, OrderRepository orderRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByProductIdDesc();
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllProducts();
        }

        return productRepository.findByProductNameContainingIgnoreCaseOrderByProductIdDesc(keyword.trim());
    }

    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham voi id=" + productId));
    }

    @Transactional
    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product payload khong duoc null");
        }

        product.setProductId(null);
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }

        product.setCategory(resolveCategory(product.getCategory()));
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Integer productId, Product payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Product payload khong duoc null");
        }

        Product existing = getProductById(productId);
        existing.setProductName(payload.getProductName());
        existing.setDescription(payload.getDescription());
        existing.setPrice(payload.getPrice());
        existing.setStock(payload.getStock());
        existing.setImage(payload.getImage());

        if (payload.getCreatedAt() != null) {
            existing.setCreatedAt(payload.getCreatedAt());
        }

        existing.setCategory(resolveCategory(payload.getCategory()));
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(Integer productId) {
        Product existing = getProductById(productId);
        productRepository.delete(existing);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay don hang voi id=" + orderId));
    }

    @Transactional
    public Order updateOrderStatus(Integer orderId, Order.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status khong duoc null");
        }

        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public long countProducts() {
        return productRepository.count();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByCategoryNameAsc();
    }

    public long countOrders() {
        return orderRepository.count();
    }

    public long countOrdersByStatus(Order.Status status) {
        if (status == null) {
            return 0L;
        }

        return orderRepository.countByStatus(status);
    }

    private Category resolveCategory(Category category) {
        if (category == null || category.getCategoryId() == null) {
            return null;
        }

        Integer categoryId = category.getCategoryId();
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay danh muc voi id=" + categoryId));
    }
}
