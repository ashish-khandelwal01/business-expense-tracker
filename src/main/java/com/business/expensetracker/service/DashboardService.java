package com.business.expensetracker.service;

import com.business.expensetracker.entity.Expense;
import com.business.expensetracker.entity.Sale;
import com.business.expensetracker.repository.ExpenseRepository;
import com.business.expensetracker.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SaleRepository saleRepository;

    public Map<String, Object> getDashboardSummary() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // Get current month data
        LocalDate monthStart = YearMonth.of(currentYear, currentMonth).atDay(1);
        LocalDate monthEnd = YearMonth.of(currentYear, currentMonth).atEndOfMonth();

        BigDecimal monthlyExpenses = calculateMonthlyExpenses(monthStart, monthEnd);
        BigDecimal monthlySales = calculateMonthlySales(monthStart, monthEnd);
        BigDecimal monthlyOverhead = calculateMonthlyOverhead(monthStart, monthEnd);

        // Calculate gross profit (sales - direct costs: inventory + running costs)
        BigDecimal directCosts = calculateDirectCosts(monthStart, monthEnd);
        BigDecimal grossProfit = monthlySales.subtract(directCosts);

        // Calculate net profit (gross profit - overhead depreciation)
        BigDecimal netProfit = grossProfit.subtract(monthlyOverhead);

        Map<String, Object> summary = new HashMap<>();
        summary.put("monthlyExpenses", monthlyExpenses);
        summary.put("monthlySales", monthlySales);
        summary.put("directCosts", directCosts);
        summary.put("monthlyOverhead", monthlyOverhead);
        summary.put("grossProfit", grossProfit);
        summary.put("netProfit", netProfit);
        summary.put("profitMargin", calculateProfitMargin(netProfit, monthlySales));

        return summary;
    }

    public Map<String, Object> getMonthlyData() {
        int currentYear = LocalDate.now().getYear();
        
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            LocalDate start = yearMonth.atDay(1);
            LocalDate end = yearMonth.atEndOfMonth();

            BigDecimal sales = calculateMonthlySales(start, end);
            BigDecimal expenses = calculateMonthlyExpenses(start, end);
            BigDecimal overhead = calculateMonthlyOverhead(start, end);
            BigDecimal directCosts = calculateDirectCosts(start, end);
            BigDecimal grossProfit = sales.subtract(directCosts);
            BigDecimal netProfit = grossProfit.subtract(overhead);

            Map<String, Object> data = new HashMap<>();
            data.put("month", month);
            data.put("monthName", yearMonth.getMonth().name());
            data.put("sales", sales);
            data.put("expenses", expenses);
            data.put("directCosts", directCosts);
            data.put("overhead", overhead);
            data.put("grossProfit", grossProfit);
            data.put("netProfit", netProfit);

            monthlyData.add(data);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("year", currentYear);
        result.put("data", monthlyData);
        return result;
    }

    public Map<String, BigDecimal> getExpensesByCategory() {
        List<Expense> expenses = expenseRepository.findAll();
        
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().name(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add
                        )
                ));
    }

    public List<Map<String, Object>> getTopProducts() {
        List<Sale> sales = saleRepository.findAll();
        
        Map<Long, Map<String, Object>> productSales = new HashMap<>();
        
        for (Sale sale : sales) {
            if (sale.getProduct() != null) {
                Long productId = sale.getProduct().getId();
                productSales.putIfAbsent(productId, new HashMap<>());
                Map<String, Object> productData = productSales.get(productId);
                
                productData.put("productId", productId);
                productData.put("productName", sale.getProduct().getProductName());
                
                int currentQuantity = (int) productData.getOrDefault("totalQuantity", 0);
                productData.put("totalQuantity", currentQuantity + sale.getQuantity());
                
                BigDecimal currentRevenue = (BigDecimal) productData.getOrDefault("totalRevenue", BigDecimal.ZERO);
                productData.put("totalRevenue", currentRevenue.add(sale.getTotalAmount()));
            }
        }
        
        return productSales.values().stream()
                .sorted((a, b) -> ((BigDecimal) b.get("totalRevenue")).compareTo((BigDecimal) a.get("totalRevenue")))
                .limit(10)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateMonthlySales(LocalDate start, LocalDate end) {
        List<Sale> sales = saleRepository.findBySaleDateBetween(start, end);
        return sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMonthlyExpenses(LocalDate start, LocalDate end) {
        BigDecimal total = expenseRepository.getTotalExpenseInRange(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    private BigDecimal calculateMonthlyOverhead(LocalDate start, LocalDate end) {
        List<Expense> overheadExpenses = expenseRepository.findAll().stream()
                .filter(e -> e.getCategory() == Expense.ExpenseCategory.OVERHEAD)
                .filter(e -> isOverheadActiveInPeriod(e, start, end))
                .collect(Collectors.toList());

        return overheadExpenses.stream()
                .map(e -> e.getMonthlyDepreciation() != null ? e.getMonthlyDepreciation() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDirectCosts(LocalDate start, LocalDate end) {
        List<Expense> directExpenses = expenseRepository.findAll().stream()
                .filter(e -> e.getCategory() == Expense.ExpenseCategory.INVENTORY || 
                            e.getCategory() == Expense.ExpenseCategory.RUNNING_COSTS)
                .filter(e -> !e.getExpenseDate().isBefore(start) && !e.getExpenseDate().isAfter(end))
                .collect(Collectors.toList());

        return directExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isOverheadActiveInPeriod(Expense expense, LocalDate start, LocalDate end) {
        if (expense.getStartDate() == null || expense.getEndDate() == null) {
            return false;
        }
        // Check if overhead period overlaps with the given period
        return !expense.getEndDate().isBefore(start) && !expense.getStartDate().isAfter(end);
    }

    private BigDecimal calculateProfitMargin(BigDecimal profit, BigDecimal sales) {
        if (sales.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return profit.divide(sales, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    }
}

