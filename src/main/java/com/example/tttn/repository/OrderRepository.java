package com.example.tttn.repository;

//Import kiểu List để trả về danh sách đơn hàng.
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tttn.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @EntityGraph(attributePaths = { "user" })
    List<Order> findAllByOrderByOrderDateDesc();

    long countByStatus(Order.Status status);
}
