package com.delivery.project.region.repository;

import com.delivery.project.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {
}
