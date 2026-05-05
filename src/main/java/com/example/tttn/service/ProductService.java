package com.example.tttn.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tttn.dto.ProductForm;
import com.example.tttn.model.Category;
import com.example.tttn.model.Order;
import com.example.tttn.model.Product;
import com.example.tttn.repository.CategoryRepository;
import com.example.tttn.repository.OrderRepository;
import com.example.tttn.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, OrderRepository orderRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
    }

    // ==================== SAN PHAM ====================
    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByProductIdDesc();
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllProducts();
        }
        return productRepository.findByProductNameContainingIgnoreCaseOrderByProductIdDesc(keyword.trim());
    }

    // lay san pham theo id 
    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham voi id=" + productId));
    }

    //ham luu san pham
    @Transactional
    public Product createProduct(ProductForm form, String imagePath) {
        Product product = buildProduct(form, imagePath);
        product.setCreatedAt(LocalDateTime.now());
        product.setCategory(resolveCategory(product.getCategory()));
        return productRepository.save(product);
    }

    //câp nhât sản phẩm
    @Transactional
    public Product updateProduct(Integer productId, ProductForm form, String imagePath) {
        Product existing = getProductById(productId);
        existing.setProductName(normalizeText(form.getProductName()));
        existing.setDescription(normalizeText(form.getDescription()));
        existing.setPrice(form.getPrice());
        existing.setStock(form.getStock());
        existing.setImage(normalizeText(imagePath));
        existing.setCategory(resolveCategory(buildCategory(form.getCategoryId())));
        return productRepository.save(existing);
    }

    //xoa san pham theo id
    @Transactional
    public void deleteProduct(Integer productId) {
        productRepository.delete(getProductById(productId));
    }

    //kiem tra du lieu form
    public String validateProductForm(ProductForm form) {
        if (form == null) {
            return "Dữ liệu không hợp lệ.";
        }

        String productName = normalizeText(form.getProductName());
        if (productName == null) {
            return "Tên sản phẩm không được để trống.";
        }
        if (productName.length() > 150) {
            return "Tên sản phẩm tối đa 150 ký tự.";
        }

        String description = normalizeText(form.getDescription());
        if (description != null && description.length() > 500) {
            return "Mô tả tối đa 500 ký tự.";
        }

        if (form.getPrice() == null) {
            return "Giá sản phẩm không được để trống.";
        }
        if (form.getPrice().signum() < 0) {
            return "Giá sản phẩm phải lớn hơn hoặc bằng 0.";
        }

        if (form.getStock() == null) {
            return "Số lượng tồn kho không được để trống.";
        }
        if (form.getStock() < 0) {
            return "Số lượng tồn kho phải lớn hơn hoặc bằng 0.";
        }

        if (form.getCategoryId() != null && form.getCategoryId() <= 0) {
            return "Danh mục không hợp lệ.";
        }

        return null;
    }

    // ==================== DON HANG ====================
    //lay tat ca don hang
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay don hang voi id=" + orderId));
    }

    //lay thong tin chi tiet don hang theo id don hang
    public Order getOrderWithDetails(Integer orderId) {
        return orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay don hang voi id=" + orderId));
    }

    //ham cap nhat trang thai don hang
    @Transactional
    public Order updateOrderStatus(Integer orderId, Order.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status khong duoc null");
        }
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // ==================== THONG KE ====================
    //dem tong san pham 
    public long countProducts() {
        return productRepository.count();
    }
    //dem so don hang
    public long countOrders() {
        return orderRepository.count();
    }
    
    //dem don hang theo trang tthai
    public long countOrdersByStatus(Order.Status status) {
        if (status == null) {
            return 0L;
        }
        return orderRepository.countByStatus(status);
    }

    // ds danh muc
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByCategoryNameAsc();
    }

    // ==================== PRIVATE HELPERS ====================
    //lay du lieu tu form chuyen qua entity
    private Product buildProduct(ProductForm form, String imagePath) {
        Product product = new Product();
        product.setProductName(normalizeText(form.getProductName()));
        product.setDescription(normalizeText(form.getDescription()));
        product.setPrice(form.getPrice());
        product.setStock(form.getStock());
        product.setImage(normalizeText(imagePath));
        product.setCategory(buildCategory(form.getCategoryId()));
        return product;
    }

    //ham tao ra object 
    private Category buildCategory(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setCategoryId(categoryId);
        return category;
    }

    //ham lam sach chuoi ky tu du vao
    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    //ham kiêm tra danh muc co ton tai hay khong
    private Category resolveCategory(Category category) {
        if (category == null || category.getCategoryId() == null) {
            return null;
        }
        return categoryRepository.findById(category.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay danh muc voi id=" + category.getCategoryId()));
    }
}
