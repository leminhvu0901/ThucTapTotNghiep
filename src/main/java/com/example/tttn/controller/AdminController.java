package com.example.tttn.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tttn.dto.ProductForm;
import com.example.tttn.model.Category;
import com.example.tttn.model.Order;
import com.example.tttn.model.Product;
import com.example.tttn.service.ImageStorageService;
import com.example.tttn.service.ProductService;
import com.example.tttn.service.ResourceNotFoundException;

@Controller
public class AdminController {

    private final ProductService productService;
    private final ImageStorageService imageStorageService;

    public AdminController(ProductService productService, ImageStorageService imageStorageService) {
        this.productService = productService;
        this.imageStorageService = imageStorageService;
    }

    @ModelAttribute("productForm")
    public ProductForm productForm() {
        return new ProductForm();
    }

    @GetMapping({"/admin"})
    public String homeAdmin(Model model) {
        loadDashboardStats(model);
        return "admin/homeAdmin";
    }

    @GetMapping({"/admin/sanpham"})
    public String adminSanpham(
            @RequestParam(name = "keyword", required = false) String keyword, //key tim kiem
            @RequestParam(name = "editId", required = false) Integer editId,  // lay du lieu cho form chinh sua 
            Model model) {

        model.addAttribute("products", productService.searchProducts(keyword));
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("keyword", keyword == null ? "" : keyword.trim());

        //kiem tra du lieu form 
        if (editId != null) {
            try {
                model.addAttribute("editingProduct", productService.getProductById(editId));
            } catch (ResourceNotFoundException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        return "admin/sanpham";
    }

    @GetMapping({"/admin/sanpham/them"})
    public String adminThemSanpham(Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/themsanpham";
    }

    @PostMapping({"/admin/sanpham", "/admin/sanpham/them"})
    public String createProduct(
            @ModelAttribute("productForm") ProductForm productForm,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        String validationMessage = validateProductForm(productForm);
        if (validationMessage != null) {
            model.addAttribute("error", validationMessage);
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/themsanpham";
        }

        try {
            String imagePath = imageStorageService.storeProductImage(imageFile);
            Product payload = mapToProduct(productForm, imagePath);
            productService.createProduct(payload);
            redirectAttributes.addFlashAttribute("message", "Them san pham thanh cong.");
            return "redirect:/admin/sanpham";
        } catch (RuntimeException ex) {
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("error", ex.getMessage());
            return "admin/themsanpham";
        }
    }

    @PostMapping({"/admin/sanpham/{productId}/capnhat"})
    public String updateProduct(
            @PathVariable Integer productId,
            @ModelAttribute("productForm") ProductForm productForm,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        String validationMessage = validateProductForm(productForm);
        if (validationMessage != null) {
            redirectAttributes.addFlashAttribute("error", validationMessage);
            return "redirect:/admin/sanpham?editId=" + productId;
        }

        try {
            Product currentProduct = productService.getProductById(productId);
            String imagePath = imageStorageService.storeProductImage(imageFile);
            if (imagePath == null) {
                imagePath = currentProduct.getImage();
            }

            Product payload = mapToProduct(productForm, imagePath);
            productService.updateProduct(productId, payload);
            redirectAttributes.addFlashAttribute("message", "Cap nhat san pham thanh cong.");
            return "redirect:/admin/sanpham";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/sanpham?editId=" + productId;
        }
    }

    @PostMapping({"/admin/sanpham/{productId}/xoa"})
    public String deleteProduct(@PathVariable Integer productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(productId);
            redirectAttributes.addFlashAttribute("message", "Xoa san pham thanh cong.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/admin/sanpham";
    }

    @GetMapping({"/admin/donhang"})
    public String adminDonHang(Model model) {
        List<Order> orders = productService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", Order.Status.values());

        return "admin/donhang";
    }

    @PostMapping({"/admin/donhang/{orderId}/trangthai"})
    public String updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam("status") Order.Status status,
            RedirectAttributes redirectAttributes) {

        try {
            productService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("message", "Cap nhat trang thai don hang thanh cong.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/admin/donhang";
    }

    private void loadDashboardStats(Model model) {
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("totalOrders", productService.countOrders());
        model.addAttribute("pendingOrders", productService.countOrdersByStatus(Order.Status.pending));
        model.addAttribute("confirmedOrders", productService.countOrdersByStatus(Order.Status.confirmed));
        model.addAttribute("shippingOrders", productService.countOrdersByStatus(Order.Status.shipping));
        model.addAttribute("completedOrders", productService.countOrdersByStatus(Order.Status.completed));
        model.addAttribute("cancelledOrders", productService.countOrdersByStatus(Order.Status.cancelled));
    }

    private Product mapToProduct(ProductForm productForm, String imagePath) {

        Product product = new Product();
        product.setProductName(normalizeText(productForm.getProductName()));
        product.setDescription(normalizeText(productForm.getDescription()));
        product.setPrice(productForm.getPrice());
        product.setStock(productForm.getStock());
        product.setImage(normalizeText(imagePath));

        if (productForm.getCategoryId() != null) {
            Category category = new Category();
            category.setCategoryId(productForm.getCategoryId());
            product.setCategory(category);
        }

        return product;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String validateProductForm(ProductForm productForm) {
        if (productForm == null) {
            return "Du lieu khong hop le.";
        }

        String productName = normalizeText(productForm.getProductName());
        if (productName == null) {
            return "Ten san pham khong duoc de trong.";
        }

        if (productName.length() > 150) {
            return "Ten san pham toi da 150 ky tu.";
        }

        String description = normalizeText(productForm.getDescription());
        if (description != null && description.length() > 500) {
            return "Mo ta toi da 500 ky tu.";
        }

        if (productForm.getPrice() == null) {
            return "Gia san pham khong duoc de trong.";
        }

        if (productForm.getPrice().signum() < 0) {
            return "Gia san pham phai lon hon hoac bang 0.";
        }

        if (productForm.getStock() == null) {
            return "So luong ton kho khong duoc de trong.";
        }

        if (productForm.getStock() < 0) {
            return "So luong ton kho phai lon hon hoac bang 0.";
        }

        if (productForm.getCategoryId() != null && productForm.getCategoryId() <= 0) {
            return "Danh muc khong hop le.";
        }

        return null;
    }

}
