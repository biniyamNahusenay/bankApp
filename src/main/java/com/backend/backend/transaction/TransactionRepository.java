package com.backend.backend.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTop5ByPhoneOrderByCreatedDateDesc(String phone); // For home page
    List<Transaction> findByPhoneOrderByCreatedDateDesc(String phone); // For history (non-paged)
    Page<Transaction> findByPhoneOrderByCreatedDateDesc(String phone, Pageable pageable); // Paged history
}
