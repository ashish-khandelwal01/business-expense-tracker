package com.business.expensetracker.repository;

import com.business.expensetracker.entity.ReturnMonthly;
import com.business.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReturnMonthlyRepository extends JpaRepository<ReturnMonthly, Long> {
    List<ReturnMonthly> findByReturnDateBetweenAndOwner(LocalDate start, LocalDate end, User owner);
    java.util.Optional<ReturnMonthly> findByIdAndOwner(Long id, User owner);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ReturnMonthly r SET r.owner = :owner WHERE r.owner IS NULL")
    void assignUnownedRecords(@org.springframework.data.repository.query.Param("owner") User owner);
}
