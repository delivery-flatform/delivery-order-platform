package com.delivery.project.user.repository;

import com.delivery.project.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
