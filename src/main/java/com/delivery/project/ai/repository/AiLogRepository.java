package com.delivery.project.ai.repository;

import com.delivery.project.ai.entity.AiLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AiLogRepository extends JpaRepository<AiLog, UUID> {

    Page<AiLog> findByPromptContainingOrResponseContaining(String prompt, String response, Pageable pageable);
}
