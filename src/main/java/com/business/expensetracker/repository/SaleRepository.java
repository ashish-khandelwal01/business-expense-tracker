package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Sale;
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
    List<Sale> findBySaleDateBetween(LocalDate start, LocalDate end);
    List<Sale> findByProductId(Long productId);

    @EntityGraph(attributePaths = "product")
    Page<Sale> findBySaleDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    @EntityGraph(attributePaths = "product")
    Page<Sale> findByProductId(Long productId, Pageable pageable);

    @Query("SELECT s.product.id, s.product.productName, SUM(s.quantity), SUM(s.totalAmount) " +
           "FROM Sale s GROUP BY s.product.id, s.product.productName " +
           "ORDER BY SUM(s.totalAmount) DESC")
    List<Object[]> findTopProductsByRevenue(Pageable pageable);

    @Query(value = "SELECT EXTRACT(MONTH FROM sale_date) AS month, SUM(total_amount) AS total " +
                   "FROM sales WHERE EXTRACT(YEAR FROM sale_date) = :year " +
                   "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlySalesTotals(@Param("year") int year);
}
