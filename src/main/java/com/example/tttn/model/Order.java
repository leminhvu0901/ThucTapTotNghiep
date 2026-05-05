package com.example.tttn.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;

	@Column(name = "order_date")
	private LocalDateTime orderDate;

	@Column(name = "total_amount", precision = 10, scale = 2)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.pending;

	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "full_name", length = 100)
	private String fullName;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "phone", length = 15)
	private String phone;

	@Column(name = "address", length = 255)
	private String address;

	@OneToMany(mappedBy = "order")
	private List<OrderDetail> orderDetails;

	public enum Status {
		pending,
		confirmed,
		shipping,
		completed,
		cancelled
	}

	public Order() {
	}

	public Order(Integer orderId, LocalDateTime orderDate, BigDecimal totalAmount, Status status, User user) {
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
		this.status = status;
		this.user = user;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	//Hung code
	public BigDecimal getCalculatedTotal() {
		if (orderDetails == null || orderDetails.isEmpty()) return BigDecimal.ZERO;
		return orderDetails.stream()
				.filter(d -> d.getPrice() != null && d.getQuantity() != null)
				.map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
