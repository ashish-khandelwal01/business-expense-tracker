package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Sale;
import com.business.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByOwner(User owner);
    List<Sale> findBySaleDateBetweenAndOwner(LocalDate start, LocalDate end, User owner);
    java.util.Optional<Sale> findByIdAndOwner(Long id, User owner);

    @EntityGraph(attributePaths = "product")
    Page<Sale> findBySaleDateBetweenAndOwner(LocalDate start, LocalDate end, User owner, Pageable pageable);

    @EntityGraph(attributePaths = "product")
    Page<Sale> findByProductIdAndOwner(Long productId, User owner, Pageable pageable);

    @Query("SELECT s.product.id, s.product.productName, SUM(s.quantity), SUM(s.totalAmount) " +
           "FROM Sale s WHERE s.owner = :owner GROUP BY s.product.id, s.product.productName " +
           "ORDER BY SUM(s.totalAmount) DESC")
    List<Object[]> findTopProductsByRevenue(@Param("owner") User owner, Pageable pageable);

    @Query(value = "SELECT EXTRACT(MONTH FROM sale_date) AS month, SUM(total_amount) AS total " +
                   "FROM sales WHERE user_id = :userId AND EXTRACT(YEAR FROM sale_date) = :year " +
                   "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlySalesTotals(@Param("userId") Long userId, @Param("year") int year);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Sale s SET s.owner = :owner WHERE s.owner IS NULL")
    void assignUnownedRecords(@Param("owner") User owner);
}
