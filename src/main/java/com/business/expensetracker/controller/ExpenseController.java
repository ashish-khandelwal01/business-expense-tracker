package com.business.expensetracker.controller;

import com.business.expensetracker.entity.Expense;
import com.business.expensetracker.entity.Product;
import com.business.expensetracker.repository.ExpenseRepository;
import com.business.expensetracker.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ExpenseController {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/expenses")
    public String expensesPage(Model model) {
        return "expenses";
    }

    @GetMapping("/api/expenses")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        List<Map<String, Object>> expenseData = expenses.stream().map(expense -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", expense.getId());
            data.put("category", expense.getCategory());
            data.put("expenseDate", expense.getExpenseDate());
            data.put("description", expense.getDescription());
            data.put("productName", expense.getProductName());
            data.put("amount", expense.getAmount());
            data.put("quantity", expense.getQuantity());
            data.put("unitCost", expense.getUnitCost());
            data.put("lumpsumAmount", expense.getLumpsumAmount());
            data.put("startDate", expense.getStartDate());
            data.put("endDate", expense.getEndDate());
            data.put("depreciationMonths", expense.getDepreciationMonths());
            data.put("monthlyDepreciation", expense.getMonthlyDepreciation());
            data.put("unitsToDistribute", expense.getUnitsToDistribute());
            data.put("costPerUnit", expense.getCostPerUnit());
            data.put("notes", expense.getNotes());
            
            if (expense.getProduct() != null) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("id", expense.getProduct().getId());
                productData.put("productName", expense.getProduct().getProductName());
                data.put("product", productData);
            }
            
            return data;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(expenseData);
    }

    @PostMapping("/api/expenses")
    @ResponseBody
    public ResponseEntity<?> addExpense(@RequestBody Map<String, Object> expenseData) {
        try {
            Expense expense = new Expense();
            expense.setCategory(Expense.ExpenseCategory.valueOf((String) expenseData.get("category")));
            expense.setExpenseDate(java.time.LocalDate.parse((String) expenseData.get("expenseDate")));
            expense.setDescription((String) expenseData.get("description"));
            expense.setNotes((String) expenseData.get("notes"));
            
            // Set productName for Overhead and Running Costs
            if (expenseData.containsKey("productName")) {
                expense.setProductName((String) expenseData.get("productName"));
            }
            
            // Set product if provided
            if (expenseData.get("productId") != null) {
                Long productId = Long.valueOf(expenseData.get("productId").toString());
                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
                expense.setProduct(product);
            }
            
            // Handle different expense types
            if (expense.getCategory() == Expense.ExpenseCategory.INVENTORY) {
                expense.setQuantity(expenseData.get("quantity") != null ? Integer.valueOf(expenseData.get("quantity").toString()) : null);
                expense.setUnitCost(expenseData.get("unitCost") != null ? new java.math.BigDecimal(expenseData.get("unitCost").toString()) : null);
            } else if (expense.getCategory() == Expense.ExpenseCategory.OVERHEAD) {
                expense.setLumpsumAmount(new java.math.BigDecimal(expenseData.get("lumpsumAmount").toString()));
                expense.setStartDate(java.time.LocalDate.parse((String) expenseData.get("startDate")));
                expense.setEndDate(java.time.LocalDate.parse((String) expenseData.get("endDate")));
                expense.setDepreciationMonths(Integer.valueOf(expenseData.get("depreciationMonths").toString()));
            } else if (expense.getCategory() == Expense.ExpenseCategory.RUNNING_COSTS) {
                expense.setAmount(new java.math.BigDecimal(expenseData.get("amount").toString()));
                expense.setUnitsToDistribute(Integer.valueOf(expenseData.get("unitsToDistribute").toString()));
            }
            
            Expense saved = expenseRepository.save(expense);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/api/expenses/{id}")
    @ResponseBody
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody Map<String, Object> expenseData) {
        try {
            Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
                
            expense.setCategory(Expense.ExpenseCategory.valueOf((String) expenseData.get("category")));
            expense.setExpenseDate(java.time.LocalDate.parse((String) expenseData.get("expenseDate")));
            expense.setDescription((String) expenseData.get("description"));
            expense.setNotes((String) expenseData.get("notes"));
            
            // Set productName for Overhead and Running Costs
            if (expenseData.containsKey("productName")) {
                expense.setProductName((String) expenseData.get("productName"));
            } else {
                expense.setProductName(null);
            }
            
            // Set product if provided
            if (expenseData.get("productId") != null) {
                Long productId = Long.valueOf(expenseData.get("productId").toString());
                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
                expense.setProduct(product);
            }
            
            // Clear fields based on category
            expense.setQuantity(null);
            expense.setUnitCost(null);
            expense.setLumpsumAmount(null);
            expense.setStartDate(null);
            expense.setEndDate(null);
            expense.setDepreciationMonths(null);
            expense.setUnitsToDistribute(null);
            expense.setAmount(null);
            
            // Handle different expense types
            if (expense.getCategory() == Expense.ExpenseCategory.INVENTORY) {
                expense.setQuantity(expenseData.get("quantity") != null ? Integer.valueOf(expenseData.get("quantity").toString()) : null);
                expense.setUnitCost(expenseData.get("unitCost") != null ? new java.math.BigDecimal(expenseData.get("unitCost").toString()) : null);
            } else if (expense.getCategory() == Expense.ExpenseCategory.OVERHEAD) {
                expense.setLumpsumAmount(new java.math.BigDecimal(expenseData.get("lumpsumAmount").toString()));
                expense.setStartDate(java.time.LocalDate.parse((String) expenseData.get("startDate")));
                expense.setEndDate(java.time.LocalDate.parse((String) expenseData.get("endDate")));
                expense.setDepreciationMonths(Integer.valueOf(expenseData.get("depreciationMonths").toString()));
            } else if (expense.getCategory() == Expense.ExpenseCategory.RUNNING_COSTS) {
                expense.setAmount(new java.math.BigDecimal(expenseData.get("amount").toString()));
                expense.setUnitsToDistribute(Integer.valueOf(expenseData.get("unitsToDistribute").toString()));
            }
            
            Expense updated = expenseRepository.save(expense);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/expenses/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        try {
            expenseRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

