package com.business.expensetracker.repository;

import com.business.expensetracker.entity.Product;
import com.business.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOwner(User owner);
    Optional<Product> findByIdAndOwner(Long id, User owner);
    Optional<Product> findByProductNameAndOwner(String productName, User owner);
    boolean existsByProductNameAndOwner(String productName, User owner);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Product p SET p.owner = :owner WHERE p.owner IS NULL")
    void assignUnownedRecords(@org.springframework.data.repository.query.Param("owner") User owner);
}
