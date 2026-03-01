package com.business.expensetracker.service;

import com.business.expensetracker.entity.ReturnMonthly;
import com.business.expensetracker.repository.ReturnMonthlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReturnMonthlyService {

    @Autowired
    private ReturnMonthlyRepository returnMonthlyRepository;

    /**
     * Add a new return record for the day
     */
    public ReturnMonthly addReturnRecord(String product, LocalDate returnDate, Integer quantity) {
        ReturnMonthly returnMonthly = new ReturnMonthly();
        // Normalize product name to lowercase
        returnMonthly.setProduct(product != null ? product.trim().toLowerCase() : product);
        returnMonthly.setReturnDate(returnDate);
        returnMonthly.setQuantity(quantity);
        return returnMonthlyRepository.save(returnMonthly);
    }

    /**
     * Get all return records for a specific month
     */
    public List<ReturnMonthly> getMonthlyRecords(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return returnMonthlyRepository.findByReturnDateBetween(startDate, endDate);
    }

    /**
     * Get return records by product for a specific month
     */
    public List<ReturnMonthly> getMonthlyRecordsByProduct(int year, int month, String product) {
        List<ReturnMonthly> records = getMonthlyRecords(year, month);
        String normalizedProduct = product != null ? product.trim().toLowerCase() : product;
        return records.stream()
                .filter(record -> record.getProduct().equals(normalizedProduct))
                .collect(Collectors.toList());
    }

    /**
     * Get all return records for a specific date
     */
    public List<ReturnMonthly> getDailyRecords(LocalDate returnDate) {
        return returnMonthlyRepository.findByReturnDateBetween(returnDate, returnDate);
    }

    /**
     * Update an existing return record
     */
    public ReturnMonthly updateReturnRecord(Long id, String product, LocalDate returnDate, Integer quantity) {
        ReturnMonthly returnMonthly = returnMonthlyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return record not found"));
        returnMonthly.setProduct(product != null ? product.trim().toLowerCase() : product);
        returnMonthly.setReturnDate(returnDate);
        returnMonthly.setQuantity(quantity);
        return returnMonthlyRepository.save(returnMonthly);
    }

    /**
     * Delete a return record
     */
    public void deleteReturnRecord(Long id) {
        returnMonthlyRepository.deleteById(id);
    }

    /**
     * Get summary statistics for a month
     */
    public Map<String, Object> getMonthlyStatistics(int year, int month) {
        List<ReturnMonthly> records = getMonthlyRecords(year, month);

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalRecords", records.size());
        stats.put("totalQuantity", records.stream().mapToInt(ReturnMonthly::getQuantity).sum());
        stats.put("totalDays", records.stream().map(ReturnMonthly::getReturnDate).distinct().count());

        stats.put("productBreakdown", records.stream()
                .collect(Collectors.groupingBy(
                        ReturnMonthly::getProduct,
                        Collectors.summingInt(ReturnMonthly::getQuantity)
                ))
        );

        return stats;
    }
}

