package com.business.expensetracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expenses", indexes = {
    @Index(name = "idx_expense_date", columnList = "expenseDate"),
    @Index(name = "idx_expense_category", columnList = "category"),
    @Index(name = "idx_expense_product_id", columnList = "product_id")
})
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false)
    private LocalDate expenseDate;

    private String description;

    // Product name (for Overhead and Running Costs)
    private String productName;

    // Common fields
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    // Inventory-specific fields
    @ManyToOne(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Integer quantity;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal unitCost;

    // Overhead-specific fields
    @Column(precision = 19, scale = 2)
    private BigDecimal lumpsumAmount;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer depreciationMonths;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal monthlyDepreciation;

    // Running cost-specific fields
    private Integer unitsToDistribute;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal costPerUnit;

    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateDerivedFields();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateDerivedFields();
    }

    private void calculateDerivedFields() {
        if (category == ExpenseCategory.OVERHEAD && lumpsumAmount != null && depreciationMonths != null && depreciationMonths > 0) {
            monthlyDepreciation = lumpsumAmount.divide(new BigDecimal(depreciationMonths), 2, java.math.RoundingMode.HALF_UP);
            amount = lumpsumAmount;
        }
        
        if (category == ExpenseCategory.INVENTORY && quantity != null && unitCost != null) {
            amount = unitCost.multiply(new BigDecimal(quantity));
        }
        
        if (category == ExpenseCategory.RUNNING_COSTS && unitsToDistribute != null && amount != null && unitsToDistribute > 0) {
            costPerUnit = amount.divide(new BigDecimal(unitsToDistribute), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    public enum ExpenseCategory {
        INVENTORY,
        OVERHEAD,
        RUNNING_COSTS
    }
}
