package com.business.expensetracker.repository;

import com.business.expensetracker.entity.SaleMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleMonthlyRepository extends JpaRepository<SaleMonthly, Long> {
    List<SaleMonthly> findBySaleDateBetween(LocalDate start, LocalDate end);
    List<SaleMonthly> findByProduct(String product);
}

