package com.business.expensetracker.service;

import com.business.expensetracker.entity.SaleMonthly;
import com.business.expensetracker.repository.SaleMonthlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SaleMonthlyService {

    @Autowired
    private SaleMonthlyRepository saleMonthlyRepository;

    /**
     * Add a new sale record for the day
     */
    public SaleMonthly addSaleRecord(String product, LocalDate saleDate, Integer quantity) {
        SaleMonthly saleMonthly = new SaleMonthly();
        // Normalize product name to lowercase
        saleMonthly.setProduct(product != null ? product.trim().toLowerCase() : product);
        saleMonthly.setSaleDate(saleDate);
        saleMonthly.setQuantity(quantity);
        return saleMonthlyRepository.save(saleMonthly);
    }

    /**
     * Get all sale records for a specific month
     */
    public List<SaleMonthly> getMonthlyRecords(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return saleMonthlyRepository.findBySaleDateBetween(startDate, endDate);
    }

    /**
     * Get sale records by product for a specific month
     */
    public List<SaleMonthly> getMonthlyRecordsByProduct(int year, int month, String product) {
        List<SaleMonthly> records = getMonthlyRecords(year, month);
        String normalizedProduct = product != null ? product.trim().toLowerCase() : product;
        return records.stream()
                .filter(record -> record.getProduct().equals(normalizedProduct))
                .collect(Collectors.toList());
    }

    /**
     * Get all sale records for a specific date
     */
    public List<SaleMonthly> getDailyRecords(LocalDate saleDate) {
        return saleMonthlyRepository.findBySaleDateBetween(saleDate, saleDate);
    }

    /**
     * Update an existing sale record
     */
    public SaleMonthly updateSaleRecord(Long id, String product, LocalDate saleDate, Integer quantity) {
        SaleMonthly saleMonthly = saleMonthlyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale record not found"));
        saleMonthly.setProduct(product != null ? product.trim().toLowerCase() : product);
        saleMonthly.setSaleDate(saleDate);
        saleMonthly.setQuantity(quantity);
        return saleMonthlyRepository.save(saleMonthly);
    }

    /**
     * Delete a sale record
     */
    public void deleteSaleRecord(Long id) {
        saleMonthlyRepository.deleteById(id);
    }

    /**
     * Get summary statistics for a month
     */
    public Map<String, Object> getMonthlyStatistics(int year, int month) {
        List<SaleMonthly> records = getMonthlyRecords(year, month);

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalRecords", records.size());
        stats.put("totalQuantity", records.stream().mapToInt(SaleMonthly::getQuantity).sum());
        stats.put("totalDays", records.stream().map(SaleMonthly::getSaleDate).distinct().count());

        stats.put("productBreakdown", records.stream()
                .collect(Collectors.groupingBy(
                        SaleMonthly::getProduct,
                        Collectors.summingInt(SaleMonthly::getQuantity)
                ))
        );

        return stats;
    }
}

