package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Expense;
import com.business.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByOwner(User owner);
    java.util.Optional<Expense> findByIdAndOwner(Long id, User owner);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.owner = :owner AND e.expenseDate BETWEEN :start AND :end")
    BigDecimal getTotalExpenseInRange(@Param("owner") User owner, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = "SELECT EXTRACT(MONTH FROM expense_date) AS month, SUM(amount) AS total " +
                   "FROM expenses WHERE user_id = :userId AND EXTRACT(YEAR FROM expense_date) = :year " +
                   "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlyExpenseTotals(@Param("userId") Long userId, @Param("year") int year);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Expense e SET e.owner = :owner WHERE e.owner IS NULL")
    void assignUnownedRecords(@Param("owner") User owner);
}
