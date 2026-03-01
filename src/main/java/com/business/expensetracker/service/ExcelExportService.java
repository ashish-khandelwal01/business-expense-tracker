package com.business.expensetracker.service;

import com.business.expensetracker.entity.ReturnMonthly;
import com.business.expensetracker.entity.SaleMonthly;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {

    /**
     * Export sales monthly data to Excel
     */
    public byte[] exportSalesMonthlyToExcel(int year, int month, List<SaleMonthly> records,
                                           Map<String, Object> statistics) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create Sales Details Sheet
        XSSFSheet detailsSheet = workbook.createSheet("Sales Details");
        createSalesDetailsSheet(detailsSheet, records);

        // Create Summary Sheet
        XSSFSheet summarySheet = workbook.createSheet("Summary");
        createSalesSummarySheet(summarySheet, year, month, statistics);

        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * Export returns monthly data to Excel
     */
    public byte[] exportReturnsMonthlyToExcel(int year, int month, List<ReturnMonthly> records,
                                             Map<String, Object> statistics) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create Returns Details Sheet
        XSSFSheet detailsSheet = workbook.createSheet("Returns Details");
        createReturnsDetailsSheet(detailsSheet, records);

        // Create Summary Sheet
        XSSFSheet summarySheet = workbook.createSheet("Summary");
        createReturnsSummarySheet(summarySheet, year, month, statistics);

        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * Create detailed sales data sheet
     */
    private void createSalesDetailsSheet(XSSFSheet sheet, List<SaleMonthly> records) {
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(20);

        String[] headers = {"Date", "Product", "Website", "Quantity"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(sheet.getWorkbook()));
        }

        // Add data rows
        int rowNum = 1;
        for (SaleMonthly record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getSaleDate().toString());
            row.createCell(1).setCellValue(record.getProduct());
            row.createCell(2).setCellValue(record.getWebsite());
            row.createCell(3).setCellValue(record.getQuantity());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create detailed returns data sheet
     */
    private void createReturnsDetailsSheet(XSSFSheet sheet, List<ReturnMonthly> records) {
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(20);

        String[] headers = {"Date", "Product", "Website", "Quantity"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(sheet.getWorkbook()));
        }

        // Add data rows
        int rowNum = 1;
        for (ReturnMonthly record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getReturnDate().toString());
            row.createCell(1).setCellValue(record.getProduct());
            row.createCell(2).setCellValue(record.getWebsite());
            row.createCell(3).setCellValue(record.getQuantity());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create sales summary sheet
     */
    @SuppressWarnings("unchecked")
    private void createSalesSummarySheet(XSSFSheet sheet, int year, int month, Map<String, Object> statistics) {
        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Monthly Sales Summary - " + YearMonth.of(year, month));
        titleCell.setCellStyle(createHeaderStyle(sheet.getWorkbook()));

        rowNum++; // Empty row

        // Statistics
        Row row1 = sheet.createRow(rowNum++);
        row1.createCell(0).setCellValue("Total Records:");
        row1.createCell(1).setCellValue((Integer) statistics.get("totalRecords"));

        Row row2 = sheet.createRow(rowNum++);
        row2.createCell(0).setCellValue("Total Quantity Sold:");
        row2.createCell(1).setCellValue((Integer) statistics.get("totalQuantity"));

        Row row3 = sheet.createRow(rowNum++);
        row3.createCell(0).setCellValue("Days with Sales:");
        row3.createCell(1).setCellValue(((Long) statistics.get("totalDays")).intValue());

        rowNum++; // Empty row

        // Product breakdown
        Row breakdownHeader = sheet.createRow(rowNum++);
        breakdownHeader.createCell(0).setCellValue("Product Breakdown");
        breakdownHeader.createCell(0).setCellStyle(createHeaderStyle(sheet.getWorkbook()));

        Row productHeader = sheet.createRow(rowNum++);
        productHeader.createCell(0).setCellValue("Product");
        productHeader.createCell(1).setCellValue("Total Quantity");

        Map<String, Integer> productBreakdown = (Map<String, Integer>) statistics.get("productBreakdown");
        for (Map.Entry<String, Integer> entry : productBreakdown.entrySet()) {
            Row productRow = sheet.createRow(rowNum++);
            productRow.createCell(0).setCellValue(entry.getKey());
            productRow.createCell(1).setCellValue(entry.getValue());
        }

        rowNum++; // Empty row

        // Website breakdown
        Row websiteBreakdownHeader = sheet.createRow(rowNum++);
        websiteBreakdownHeader.createCell(0).setCellValue("Website Breakdown");
        websiteBreakdownHeader.createCell(0).setCellStyle(createHeaderStyle(sheet.getWorkbook()));

        Row websiteHeader = sheet.createRow(rowNum++);
        websiteHeader.createCell(0).setCellValue("Website");
        websiteHeader.createCell(1).setCellValue("Total Quantity");

        Map<String, Integer> websiteBreakdown = (Map<String, Integer>) statistics.get("websiteBreakdown");
        for (Map.Entry<String, Integer> entry : websiteBreakdown.entrySet()) {
            Row websiteRow = sheet.createRow(rowNum++);
            websiteRow.createCell(0).setCellValue(entry.getKey());
            websiteRow.createCell(1).setCellValue(entry.getValue());
        }

        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    /**
     * Create returns summary sheet
     */
    @SuppressWarnings("unchecked")
    private void createReturnsSummarySheet(XSSFSheet sheet, int year, int month, Map<String, Object> statistics) {
        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Monthly Returns Summary - " + YearMonth.of(year, month));
        titleCell.setCellStyle(createHeaderStyle(sheet.getWorkbook()));

        rowNum++; // Empty row

        // Statistics
        Row row1 = sheet.createRow(rowNum++);
        row1.createCell(0).setCellValue("Total Records:");
        row1.createCell(1).setCellValue((Integer) statistics.get("totalRecords"));

        Row row2 = sheet.createRow(rowNum++);
        row2.createCell(0).setCellValue("Total Quantity Returned:");
        row2.createCell(1).setCellValue((Integer) statistics.get("totalQuantity"));

        Row row3 = sheet.createRow(rowNum++);
        row3.createCell(0).setCellValue("Days with Returns:");
        row3.createCell(1).setCellValue(((Long) statistics.get("totalDays")).intValue());

        rowNum++; // Empty row

        // Product breakdown
        Row breakdownHeader = sheet.createRow(rowNum++);
        breakdownHeader.createCell(0).setCellValue("Product Breakdown");
        breakdownHeader.createCell(0).setCellStyle(createHeaderStyle(sheet.getWorkbook()));

        Row productHeader = sheet.createRow(rowNum++);
        productHeader.createCell(0).setCellValue("Product");
        productHeader.createCell(1).setCellValue("Total Quantity");

        Map<String, Integer> productBreakdown = (Map<String, Integer>) statistics.get("productBreakdown");
        for (Map.Entry<String, Integer> entry : productBreakdown.entrySet()) {
            Row productRow = sheet.createRow(rowNum++);
            productRow.createCell(0).setCellValue(entry.getKey());
            productRow.createCell(1).setCellValue(entry.getValue());
        }

        rowNum++; // Empty row

        // Website breakdown
        Row websiteBreakdownHeader = sheet.createRow(rowNum++);
        websiteBreakdownHeader.createCell(0).setCellValue("Website Breakdown");
        websiteBreakdownHeader.createCell(0).setCellStyle(createHeaderStyle(sheet.getWorkbook()));

        Row websiteHeader = sheet.createRow(rowNum++);
        websiteHeader.createCell(0).setCellValue("Website");
        websiteHeader.createCell(1).setCellValue("Total Quantity");

        Map<String, Integer> websiteBreakdown = (Map<String, Integer>) statistics.get("websiteBreakdown");
        for (Map.Entry<String, Integer> entry : websiteBreakdown.entrySet()) {
            Row websiteRow = sheet.createRow(rowNum++);
            websiteRow.createCell(0).setCellValue(entry.getKey());
            websiteRow.createCell(1).setCellValue(entry.getValue());
        }

        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    /**
     * Create header cell style
     */
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor((short) 0xCCCCCC);
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}

