package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category")
    List<Object[]> getTotalByCategory();

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :start AND :end")
    BigDecimal getTotalExpenseInRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT FUNCTION('MONTH', e.expenseDate) as month, SUM(e.amount) as total FROM Expense e WHERE FUNCTION('YEAR', e.expenseDate) = :year GROUP BY FUNCTION('MONTH', e.expenseDate) ORDER BY month")
    List<Object[]> getMonthlyExpenses(@Param("year") int year);
}