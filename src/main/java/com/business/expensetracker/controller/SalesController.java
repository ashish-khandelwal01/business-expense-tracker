package com.business.expensetracker.controller;

import com.business.expensetracker.entity.Product;
import com.business.expensetracker.entity.Sale;
import com.business.expensetracker.repository.ProductRepository;
import com.business.expensetracker.repository.SaleRepository;
import com.business.expensetracker.service.ProductService;
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
public class SalesController {
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductService productService;

    @GetMapping("/sales")
    public String salesPage(Model model) {
        return "sales";
    }

    @GetMapping("/api/sales")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        List<Map<String, Object>> salesData = sales.stream().map(sale -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", sale.getId());
            data.put("saleDate", sale.getSaleDate());
            data.put("quantity", sale.getQuantity());
            data.put("unitPrice", sale.getUnitPrice());
            data.put("totalAmount", sale.getTotalAmount());
            data.put("description", sale.getDescription());
            data.put("notes", sale.getNotes());
            
            if (sale.getProduct() != null) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("id", sale.getProduct().getId());
                productData.put("productName", sale.getProduct().getProductName());
                data.put("product", productData);
            }
            
            return data;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(salesData);
    }

    @PostMapping("/api/sales")
    @ResponseBody
    public ResponseEntity<?> addSale(@RequestBody Map<String, Object> saleData) {
        try {
            Sale sale = new Sale();
            
            Long productId = Long.valueOf(saleData.get("productId").toString());
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            sale.setProduct(product);
            sale.setSaleDate(java.time.LocalDate.parse((String) saleData.get("saleDate")));
            sale.setQuantity(Integer.valueOf(saleData.get("quantity").toString()));
            sale.setUnitPrice(new java.math.BigDecimal(saleData.get("unitPrice").toString()));
            sale.setDescription((String) saleData.get("description"));
            sale.setNotes((String) saleData.get("notes"));
            
            // Reduce stock when product is sold
            int quantity = sale.getQuantity();
            productService.updateStock(productId, -quantity);
            
            Sale saved = saleRepository.save(sale);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/api/sales/{id}")
    @ResponseBody
    public ResponseEntity<?> updateSale(@PathVariable Long id, @RequestBody Map<String, Object> saleData) {
        try {
            Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
            
            // Revert previous stock change
            int oldQuantity = sale.getQuantity();
            Long oldProductId = sale.getProduct().getId();
            productService.updateStock(oldProductId, oldQuantity);
            
            // Apply new values
            Long productId = Long.valueOf(saleData.get("productId").toString());
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            sale.setProduct(product);
            sale.setSaleDate(java.time.LocalDate.parse((String) saleData.get("saleDate")));
            sale.setQuantity(Integer.valueOf(saleData.get("quantity").toString()));
            sale.setUnitPrice(new java.math.BigDecimal(saleData.get("unitPrice").toString()));
            sale.setDescription((String) saleData.get("description"));
            sale.setNotes((String) saleData.get("notes"));
            
            // Apply new stock change
            int newQuantity = sale.getQuantity();
            productService.updateStock(productId, -newQuantity);
            
            Sale updated = saleRepository.save(sale);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/sales/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSale(@PathVariable Long id) {
        try {
            Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
            
            // Restore stock when deleting sale
            Long productId = sale.getProduct().getId();
            int quantity = sale.getQuantity();
            productService.updateStock(productId, quantity);
            
            saleRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

