package com.example.tttn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tttn.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderOrderId(Integer orderId);
}
