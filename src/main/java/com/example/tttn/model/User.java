package com.example.tttn.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")

public class User {

	// @Id: danh dau day la khoa chinh (Primary Key) cua entity User.
	@Id
	// @GeneratedValue: CSDL tu dong tao gia tri tang dan cho khoa chinh.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Column: anh xa thuoc tinh userId vao cot user_id trong bang users.
	@Column(name = "user_id")
	// Kieu Integer cho id, thuong null truoc khi doi tuong duoc luu vao DB.
	private Integer userId;

	// username duoc map vao cot username.
	// nullable = false: bat buoc phai co gia tri.
	// unique = true: khong cho trung username.
	// length = 50: do dai toi da 50 ky tu.
	@Column(name = "username", nullable = false, unique = true, length = 50)
	// Ten dang nhap cua nguoi dung.
	private String username;

	// password map vao cot password, bat buoc co gia tri, toi da 255 ky tu.
	@Column(name = "password", nullable = false, length = 255)
	// Mat khau da ma hoa (nen luu dang hash thay vi plain text).
	private String password;

	// fullName map vao cot full_name, toi da 100 ky tu.
	@Column(name = "full_name", length = 100)
	// Ho ten day du cua nguoi dung.
	private String fullName;

	// email map vao cot email, toi da 100 ky tu.
	@Column(name = "email", length = 100)
	// Dia chi email cua nguoi dung.
	private String email;

	// phone map vao cot phone, toi da 15 ky tu.
	@Column(name = "phone", length = 15)
	// So dien thoai cua nguoi dung.
	private String phone;

	// address map vao cot address, toi da 255 ky tu.
	@Column(name = "address", length = 255)
	// Dia chi giao hang/ lien he cua nguoi dung.
	private String address;

	// EnumType.STRING: luu enum dang chu (user/admin), khong luu so thu tu.
	@Enumerated(EnumType.STRING)
	// role map vao cot role.
	@Column(name = "role")
	// Gia tri mac dinh khi tao moi user la role user.
	private Role role = Role.user;

	// createdAt map vao cot created_at.
	@Column(name = "created_at")
	// Thoi diem tao tai khoan (khong kem thong tin mui gio).
	private LocalDateTime createdAt;

	// Quan he 1-N: mot User co the co nhieu Order.
	@OneToMany(mappedBy = "user")
	// mappedBy = "user": field user ben entity Order la ben so huu quan he.
	private List<Order> orders;

	// Quan he 1-N: mot User co the co nhieu Cart item.
	@OneToMany(mappedBy = "user")
	// mappedBy = "user": field user ben entity Cart giu khoa ngoai.
	private List<Cart> carts;

	public enum Role {
		user,
		admin
	}

	public User() {
	}

	public User(Integer userId, String username, String password, String fullName, String email, String phone, String address,
			Role role, LocalDateTime createdAt) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.role = role;
		this.createdAt = createdAt;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Cart> getCarts() {
		return carts;
	}

	public void setCarts(List<Cart> carts) {
		this.carts = carts;
	}
}
