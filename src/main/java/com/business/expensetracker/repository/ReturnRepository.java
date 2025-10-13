package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
    List<Return> findByReturnDateBetween(LocalDate start, LocalDate end);
    List<Return> findByProductId(Long productId);
}

