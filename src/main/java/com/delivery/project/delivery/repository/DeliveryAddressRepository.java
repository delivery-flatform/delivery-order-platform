package com.delivery.project.delivery.repository;

import com.delivery.project.delivery.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, UUID> {
    List<DeliveryAddress> findByCustomerUsernameAndDeletedAtIsNull(String customerUsername);
}
