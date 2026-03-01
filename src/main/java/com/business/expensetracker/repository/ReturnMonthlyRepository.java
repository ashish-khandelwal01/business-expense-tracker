package com.business.expensetracker.repository;

import com.business.expensetracker.entity.ReturnMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReturnMonthlyRepository extends JpaRepository<ReturnMonthly, Long> {
    List<ReturnMonthly> findByReturnDateBetween(LocalDate start, LocalDate end);
    List<ReturnMonthly> findByProduct(String product);
}

