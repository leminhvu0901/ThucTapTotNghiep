package com.example.tttn.repository;

//Import kiểu List để trả về danh sách đơn hàng.
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tttn.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {


    //ds don hang
    @EntityGraph(attributePaths = { "user" })
    List<Order> findAllByOrderByOrderDateDesc();


    //ds don hang kem cac thong tin khac
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.product WHERE o.orderId = :orderId")
    Optional<Order> findWithDetailsById(@Param("orderId") Integer orderId);

    //dem trang thai
    long countByStatus(Order.Status status);
}
