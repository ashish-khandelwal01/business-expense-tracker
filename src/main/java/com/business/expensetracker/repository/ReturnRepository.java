package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Return;
import com.business.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
    List<Return> findAllByOwner(User owner);
    List<Return> findByReturnDateBetweenAndOwner(LocalDate start, LocalDate end, User owner);
    java.util.Optional<Return> findByIdAndOwner(Long id, User owner);

    @EntityGraph(attributePaths = "product")
    Page<Return> findByReturnDateBetweenAndOwner(LocalDate start, LocalDate end, User owner, Pageable pageable);

    @EntityGraph(attributePaths = "product")
    Page<Return> findByProductIdAndOwner(Long productId, User owner, Pageable pageable);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Return r SET r.owner = :owner WHERE r.owner IS NULL")
    void assignUnownedRecords(@org.springframework.data.repository.query.Param("owner") User owner);
}
