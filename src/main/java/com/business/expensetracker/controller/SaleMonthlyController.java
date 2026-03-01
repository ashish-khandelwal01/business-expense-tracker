package com.business.expensetracker.controller;

import com.business.expensetracker.entity.SaleMonthly;
import com.business.expensetracker.service.ExcelExportService;
import com.business.expensetracker.service.SaleMonthlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/sales-monthly")
public class SaleMonthlyController {

    @Autowired
    private SaleMonthlyService saleMonthlyService;

    @Autowired
    private ExcelExportService excelExportService;

    /**
     * Get all sale records for a specific month
     */
    @GetMapping("/month")
    @ResponseBody
    public ResponseEntity<?> getMonthlyRecords(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<SaleMonthly> records = saleMonthlyService.getMonthlyRecords(year, month);
            Map<String, Object> stats = saleMonthlyService.getMonthlyStatistics(year, month);

            Map<String, Object> response = new HashMap<>();
            response.put("records", records);
            response.put("statistics", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get sale records for a specific date
     */
    @GetMapping("/date")
    @ResponseBody
    public ResponseEntity<?> getDailyRecords(@RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<SaleMonthly> records = saleMonthlyService.getDailyRecords(localDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Add a new sale record
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> addSaleRecord(@RequestBody Map<String, Object> data) {
        try {
            String product = (String) data.get("product");
            String dateStr = (String) data.get("saleDate");
            Integer quantity = ((Number) data.get("quantity")).intValue();
            String website = (String) data.get("website");

            LocalDate saleDate = LocalDate.parse(dateStr);
            SaleMonthly record = saleMonthlyService.addSaleRecord(product, saleDate, quantity, website);

            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update a sale record
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateSaleRecord(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {
        try {
            String product = (String) data.get("product");
            String dateStr = (String) data.get("saleDate");
            Integer quantity = ((Number) data.get("quantity")).intValue();
            String website = (String) data.get("website");

            LocalDate saleDate = LocalDate.parse(dateStr);
            SaleMonthly record = saleMonthlyService.updateSaleRecord(id, product, saleDate, quantity, website);

            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete a sale record
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSaleRecord(@PathVariable Long id) {
        try {
            saleMonthlyService.deleteSaleRecord(id);
            return ResponseEntity.ok(Map.of("message", "Record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Download monthly sales report as Excel
     */
    @GetMapping("/download")
    public ResponseEntity<?> downloadMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<SaleMonthly> records = saleMonthlyService.getMonthlyRecords(year, month);
            Map<String, Object> statistics = saleMonthlyService.getMonthlyStatistics(year, month);

            byte[] excelBytes = excelExportService.exportSalesMonthlyToExcel(year, month, records, statistics);

            String filename = "Sales_Report_" + YearMonth.of(year, month) + ".xlsx";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate Excel file: " + e.getMessage()));
        }
    }

    /**
     * Get statistics for a specific month
     */
    @GetMapping("/statistics")
    @ResponseBody
    public ResponseEntity<?> getStatistics(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            Map<String, Object> statistics = saleMonthlyService.getMonthlyStatistics(year, month);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

