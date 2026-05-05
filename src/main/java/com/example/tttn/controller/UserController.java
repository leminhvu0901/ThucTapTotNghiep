package com.example.tttn.controller;

import com.example.tttn.service.ProductService;
import com.example.tttn.model.Cart;
import com.example.tttn.model.Product;
import com.example.tttn.model.User;
import com.example.tttn.model.Order;
import com.example.tttn.repository.OrderRepository;
import com.example.tttn.service.CartService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@SessionAttributes("cart")
public class UserController {

    private final ProductService productService;
    private final CartService cartService;
	private final OrderRepository orderRepository; // cần tạo repository
    

    public UserController(ProductService productService, CartService cartService, OrderRepository orderRepository) {
        this.productService = productService;
        this.cartService = cartService;
		this.orderRepository = orderRepository;
    }

    @ModelAttribute("cart")
    public List<Cart> cart() {
        return new ArrayList<>();
    }

    @GetMapping("/user")
    public String homeUser(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Product> products = productService.searchProductsUser(keyword);
        model.addAttribute("products", products);
        model.addAttribute("q", keyword);
        return "user/homeUser";
    }

    @GetMapping("/user/product/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "user/chitietsp";
    }

    @PostMapping("/user/cart/add")
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            @ModelAttribute("cart") List<Cart> cart) {
        if (quantity <= 0) {
            return "redirect:/user";
        }
        Product product = productService.getProductById(productId);

        Optional<Cart> existingItem = cart.stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            Cart item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            Cart newItem = new Cart();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.add(newItem);
        }
        return "redirect:/user/giohang";
    }

    @GetMapping("/user/giohang")
    public String viewCart(@ModelAttribute("cart") List<Cart> cart, Model model) {
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", calculateCartTotal(cart));
        return "user/giohang";
    }

    private BigDecimal calculateCartTotal(List<Cart> cart) {
        return cart.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

	@PostMapping("/user/giohang/remove")
	public String removeFromCart(@RequestParam("productId") Integer productId,
                             @ModelAttribute("cart") List<Cart> cart) {
		cart.removeIf(item -> item.getProduct().getProductId().equals(productId));
		return "redirect:/user/giohang";
	}

	@GetMapping("/user/thanhtoan")
	public String checkout(@ModelAttribute("cart") List<Cart> cart,
							@ModelAttribute("user") User user,
							Model model) {
			model.addAttribute("cartItems", cart);
			model.addAttribute("cartTotal", calculateCartTotal(cart));
			model.addAttribute("user", user); // ✅ thêm user vào model
			return "user/thanhtoan";
	}


    @PostMapping("/user/thanhtoan")
    public String processCheckout(@ModelAttribute("cart") List<Cart> cart,
                                  @ModelAttribute("user") User user, // giả sử bạn có user đăng nhập
                                  Model model,
                                  SessionStatus sessionStatus) {
        if (cart.isEmpty()) {
            return "redirect:/user/giohang";
        }

		user.setUsername("guest_" + System.currentTimeMillis());
		user.setPassword("guest");
		
        // 1. Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(calculateCartTotal(cart));
        order.setStatus(Order.Status.confirmed);

        // 2. Lưu Order vào DB
        orderRepository.save(order);

        // 3. Xóa giỏ hàng trong session
        sessionStatus.setComplete();

          // 4. Thêm thông báo và quay về trang chính
		model.addAttribute("successMessage", "Thanh toán thành công! Cảm ơn bạn đã mua hàng.");
		return "redirect:/user";
    }

	@GetMapping({"/user/lichsu"})
	public String orderHistory(@ModelAttribute("user") User user, Model model) {
		List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
		model.addAttribute("orders", orders);
		return "user/lichsu"; // khớp với file lichsu.html
	}

	

}





