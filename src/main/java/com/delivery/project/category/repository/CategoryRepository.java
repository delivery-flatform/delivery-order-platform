package com.delivery.project.category.repository;

import com.delivery.project.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
