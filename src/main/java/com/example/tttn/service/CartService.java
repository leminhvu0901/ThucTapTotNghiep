package com.example.tttn.service;

import com.example.tttn.model.Cart;
import com.example.tttn.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    public void addToCart(Cart cart) {
        cartRepository.save(cart);
    }
    public List<Cart> getCartItemsForCurrentUser() {
    
    return cartRepository.findAll(); // ví dụ tạm thời
}

public BigDecimal calculateTotal(List<Cart> cartItems) {
   
    return cartItems.stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

}
