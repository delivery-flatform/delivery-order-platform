package com.delivery.project.order.repository;

import com.delivery.project.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerUsernameAndDeletedAtIsNull(String customerUsername);

    Optional<Order> findByIdAndStatus(UUID id, String status);
}
