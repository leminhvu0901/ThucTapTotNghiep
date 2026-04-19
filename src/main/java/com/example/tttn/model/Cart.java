package com.example.tttn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// @Entity: danh dau class Cart la JPA entity de map voi bang trong CSDL.
@Entity
// @Table: map entity vao bang cart.
// uniqueConstraints: khong cho phep trung cap (user_id, product_id),
// nghia la moi user chi co mot dong cart cho moi product.
@Table(name = "cart", uniqueConstraints = @UniqueConstraint(name = "uk_cart_user_product", columnNames = {
		"user_id", "product_id" }))
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Integer cartId;

	@Column(name = "quantity")
	private Integer quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	public Cart() {
	}

	public Cart(Integer cartId, Integer quantity, User user, Product product) {
		this.cartId = cartId;
		this.quantity = quantity;
		this.user = user;
		this.product = product;
	}

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
