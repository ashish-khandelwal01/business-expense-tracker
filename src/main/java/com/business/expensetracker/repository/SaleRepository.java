package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleDateBetween(LocalDate start, LocalDate end);
    List<Sale> findByProductId(Long productId);
}

