package com.example.tttn.controller;

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
    
    // ==================== DASHBOARD ====================

    @GetMapping({"/admin", "/admin/homeadmin"})
    public String homeAdmin(Model model) {
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("totalOrders", productService.countOrders());
        model.addAttribute("pendingOrders", productService.countOrdersByStatus(Order.Status.pending));
        model.addAttribute("confirmedOrders", productService.countOrdersByStatus(Order.Status.confirmed));
        model.addAttribute("shippingOrders", productService.countOrdersByStatus(Order.Status.shipping));
        model.addAttribute("completedOrders", productService.countOrdersByStatus(Order.Status.completed));
        model.addAttribute("cancelledOrders", productService.countOrdersByStatus(Order.Status.cancelled));
        return "admin/homeadmin";
    }

    // ==================== SAN PHAM ====================

    //trang chinh
    @GetMapping("/admin/sanpham")
    public String adminSanpham(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "editId", required = false) Integer editId,
            Model model) {

        model.addAttribute("products", productService.searchProducts(keyword));
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("keyword", keyword == null ? "" : keyword.trim());

        if (editId != null) {
            try {
                model.addAttribute("editingProduct", productService.getProductById(editId));
            } catch (ResourceNotFoundException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        return "admin/sanpham";
    }

    // GET hien trang them san pham
    @GetMapping("/admin/sanpham/them")
    public String adminThemSanpham(Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/themsanpham";
    }


    //lưu sản phẩm sau khi nhập thong tin
    @PostMapping({"/admin/sanpham", "/admin/sanpham/them"})
    public String createProduct(
            @ModelAttribute("productForm") ProductForm productForm, //nap du lieu tu form
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        String error = productService.validateProductForm(productForm);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/themsanpham";
        }

        try {
            String imagePath = imageStorageService.storeProductImage(imageFile);//tra ve duong dan luu anh
            productService.createProduct(productForm, imagePath);
            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công.");
            return "redirect:/admin/sanpham";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/themsanpham";
        }
    }

    @PostMapping("/admin/sanpham/{productId}/capnhat")
    public String updateProduct(
            @PathVariable Integer productId,
            @ModelAttribute("productForm") ProductForm productForm,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        String error = productService.validateProductForm(productForm);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/admin/sanpham?editId=" + productId;
        }

        try {
            Product current = productService.getProductById(productId);
            String oldImagePath = current.getImage();
            String newImagePath = imageStorageService.storeProductImage(imageFile);

            if (newImagePath != null) {
                imageStorageService.deleteProductImage(oldImagePath);
            }

            String finalImagePath = newImagePath != null ? newImagePath : oldImagePath;
            productService.updateProduct(productId, productForm, finalImagePath);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công.");
            return "redirect:/admin/sanpham";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/sanpham?editId=" + productId;
        }
    }

    @PostMapping("/admin/sanpham/{productId}/xoa")
    public String deleteProduct(@PathVariable Integer productId, RedirectAttributes redirectAttributes) {
        try {
            String imagePath = productService.getProductById(productId).getImage();
            productService.deleteProduct(productId);
            imageStorageService.deleteProductImage(imagePath);
            redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/sanpham";
    }

    // ==================== DON HANG ====================

    @GetMapping("/admin/donhang")
    public String adminDonHang(Model model) {
        model.addAttribute("orders", productService.getAllOrders());
        return "admin/donhang";
    }

    @GetMapping("/admin/donhang/{orderId}")
    public String adminChiTietDonHang(@PathVariable Integer orderId, Model model) {
        try {
            model.addAttribute("order", productService.getOrderWithDetails(orderId));
            model.addAttribute("orderStatuses", Order.Status.values());
        } catch (ResourceNotFoundException ex) {
            return "redirect:/admin/donhang";
        }
        return "admin/chitietdonhang";
    }

    @PostMapping("/admin/donhang/{orderId}/trangthai")
    public String updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam("status") Order.Status status,
            @RequestParam(value = "redirect", defaultValue = "list") String redirect,
            RedirectAttributes redirectAttributes) {

        try {
            productService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái đơn hàng thành công.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        if ("detail".equals(redirect)) {
            return "redirect:/admin/donhang/" + orderId;
        }
        return "redirect:/admin/donhang";
    }
}
