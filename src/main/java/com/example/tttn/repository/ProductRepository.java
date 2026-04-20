// Khai bao package cua file repository nay.
// Day la tang truy cap du lieu (lam viec voi database).
package com.example.tttn.repository;

// Import List de tra ve danh sach Product.
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tttn.model.Product;

// Dinh nghia repository cho Product.
// Generic thu nhat: Product  -> entity duoc quan ly.
// Generic thu hai : Integer  -> kieu du lieu cua khoa chinh (productId).
// Spring Data JPA se tu tao implementation luc runtime.
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Load kem thong tin category cua tung product khi query.
    // Lay tat ca Product va sap xep theo productId giam dan (moi nhat truoc).
    @EntityGraph(attributePaths = { "category" })
    List<Product> findAllByOrderByProductIdDesc();

    // Load kem category de tranh phat sinh nhieu query nho (N+1).
    // Tim Product co productName chua keyword, khong phan biet hoa/thuong.
    // Ket qua duoc sap xep theo productId giam dan.
    @EntityGraph(attributePaths = { "category" })
    List<Product> findByProductNameContainingIgnoreCaseOrderByProductIdDesc(String keyword);
}
