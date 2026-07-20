package com.business.expensetracker.service;

import com.business.expensetracker.entity.ReturnMonthly;
import com.business.expensetracker.repository.ReturnMonthlyRepository;
import com.business.expensetracker.entity.User;
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
    @Autowired
    private CurrentUserService currentUserService;

    /**
     * Add a new return record for the day
     */
    public ReturnMonthly addReturnRecord(String product, LocalDate returnDate, Integer quantity, String website) {
        ReturnMonthly returnMonthly = new ReturnMonthly();
        returnMonthly.setOwner(currentUserService.getCurrentUser());
        // Normalize product name to lowercase
        returnMonthly.setProduct(product != null ? product.trim().toLowerCase() : product);
        returnMonthly.setReturnDate(returnDate);
        returnMonthly.setQuantity(quantity);
        returnMonthly.setWebsite(website != null ? website.trim() : website);
        return returnMonthlyRepository.save(returnMonthly);
    }

    /**
     * Get all return records for a specific month
     */
    public List<ReturnMonthly> getMonthlyRecords(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return returnMonthlyRepository.findByReturnDateBetweenAndOwner(startDate, endDate, currentUserService.getCurrentUser());
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
        return returnMonthlyRepository.findByReturnDateBetweenAndOwner(returnDate, returnDate, currentUserService.getCurrentUser());
    }

    /**
     * Update an existing return record
     */
    public ReturnMonthly updateReturnRecord(Long id, String product, LocalDate returnDate, Integer quantity, String website) {
        ReturnMonthly returnMonthly = returnMonthlyRepository.findByIdAndOwner(id, currentUserService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Return record not found"));
        returnMonthly.setProduct(product != null ? product.trim().toLowerCase() : product);
        returnMonthly.setReturnDate(returnDate);
        returnMonthly.setQuantity(quantity);
        returnMonthly.setWebsite(website != null ? website.trim() : website);
        return returnMonthlyRepository.save(returnMonthly);
    }

    /**
     * Delete a return record
     */
    public void deleteReturnRecord(Long id) {
        ReturnMonthly returnMonthly = returnMonthlyRepository.findByIdAndOwner(id, currentUserService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Return record not found"));
        returnMonthlyRepository.delete(returnMonthly);
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

        // Group by website
        stats.put("websiteBreakdown", records.stream()
                .collect(Collectors.groupingBy(
                        ReturnMonthly::getWebsite,
                        Collectors.summingInt(ReturnMonthly::getQuantity)
                ))
        );

        return stats;
    }
}
