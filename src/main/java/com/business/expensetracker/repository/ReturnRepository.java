package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
    List<Return> findByReturnDateBetween(LocalDate start, LocalDate end);
    List<Return> findByProductId(Long productId);

    @EntityGraph(attributePaths = "product")
    Page<Return> findByReturnDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    @EntityGraph(attributePaths = "product")
    Page<Return> findByProductId(Long productId, Pageable pageable);
}
