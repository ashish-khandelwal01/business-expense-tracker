package com.business.expensetracker.repository;

import com.business.expensetracker.entity.SaleMonthly;
import com.business.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleMonthlyRepository extends JpaRepository<SaleMonthly, Long> {
    List<SaleMonthly> findBySaleDateBetweenAndOwner(LocalDate start, LocalDate end, User owner);
    java.util.Optional<SaleMonthly> findByIdAndOwner(Long id, User owner);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE SaleMonthly s SET s.owner = :owner WHERE s.owner IS NULL")
    void assignUnownedRecords(@org.springframework.data.repository.query.Param("owner") User owner);
}
