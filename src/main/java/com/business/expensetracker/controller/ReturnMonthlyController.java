package com.business.expensetracker.controller;

import com.business.expensetracker.entity.ReturnMonthly;
import com.business.expensetracker.service.ExcelExportService;
import com.business.expensetracker.service.ReturnMonthlyService;
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
@RequestMapping("/api/returns-monthly")
public class ReturnMonthlyController {

    @Autowired
    private ReturnMonthlyService returnMonthlyService;

    @Autowired
    private ExcelExportService excelExportService;

    /**
     * Get all return records for a specific month
     */
    @GetMapping("/month")
    @ResponseBody
    public ResponseEntity<?> getMonthlyRecords(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<ReturnMonthly> records = returnMonthlyService.getMonthlyRecords(year, month);
            Map<String, Object> stats = returnMonthlyService.getMonthlyStatistics(year, month);

            Map<String, Object> response = new HashMap<>();
            response.put("records", records);
            response.put("statistics", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get return records for a specific date
     */
    @GetMapping("/date")
    @ResponseBody
    public ResponseEntity<?> getDailyRecords(@RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<ReturnMonthly> records = returnMonthlyService.getDailyRecords(localDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Add a new return record
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> addReturnRecord(@RequestBody Map<String, Object> data) {
        try {
            String product = (String) data.get("product");
            String dateStr = (String) data.get("returnDate");
            Integer quantity = ((Number) data.get("quantity")).intValue();
            String website = (String) data.get("website");

            LocalDate returnDate = LocalDate.parse(dateStr);
            ReturnMonthly record = returnMonthlyService.addReturnRecord(product, returnDate, quantity, website);

            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update a return record
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateReturnRecord(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {
        try {
            String product = (String) data.get("product");
            String dateStr = (String) data.get("returnDate");
            Integer quantity = ((Number) data.get("quantity")).intValue();
            String website = (String) data.get("website");

            LocalDate returnDate = LocalDate.parse(dateStr);
            ReturnMonthly record = returnMonthlyService.updateReturnRecord(id, product, returnDate, quantity, website);

            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete a return record
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteReturnRecord(@PathVariable Long id) {
        try {
            returnMonthlyService.deleteReturnRecord(id);
            return ResponseEntity.ok(Map.of("message", "Record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Download monthly returns report as Excel
     */
    @GetMapping("/download")
    public ResponseEntity<?> downloadMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<ReturnMonthly> records = returnMonthlyService.getMonthlyRecords(year, month);
            Map<String, Object> statistics = returnMonthlyService.getMonthlyStatistics(year, month);

            byte[] excelBytes = excelExportService.exportReturnsMonthlyToExcel(year, month, records, statistics);

            String filename = "Returns_Report_" + YearMonth.of(year, month) + ".xlsx";

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
            Map<String, Object> statistics = returnMonthlyService.getMonthlyStatistics(year, month);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

