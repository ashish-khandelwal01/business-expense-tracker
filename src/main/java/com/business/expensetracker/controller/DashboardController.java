package com.business.expensetracker.controller;

import com.business.expensetracker.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        return "dashboard";
    }

    @GetMapping("/api/dashboard/summary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/api/dashboard/monthly-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMonthlyData() {
        return ResponseEntity.ok(dashboardService.getMonthlyData());
    }

    @GetMapping("/api/dashboard/expenses-by-category")
    @ResponseBody
    public ResponseEntity<Map<String, BigDecimal>> getExpensesByCategory() {
        return ResponseEntity.ok(dashboardService.getExpensesByCategory());
    }

    @GetMapping("/api/dashboard/top-products")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getTopProducts() {
        return ResponseEntity.ok(dashboardService.getTopProducts());
    }
}

