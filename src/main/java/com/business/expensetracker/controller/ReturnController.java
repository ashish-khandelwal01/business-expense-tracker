package com.business.expensetracker.controller;

import com.business.expensetracker.entity.Product;
import com.business.expensetracker.entity.Return;
import com.business.expensetracker.repository.ProductRepository;
import com.business.expensetracker.repository.ReturnRepository;
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
public class ReturnController {
    
    @Autowired
    private ReturnRepository returnRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductService productService;

    @GetMapping("/returns")
    public String returnsPage(Model model) {
        return "returns";
    }

    @GetMapping("/api/returns")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllReturns() {
        List<Return> returns = returnRepository.findAll();
        List<Map<String, Object>> returnsData = returns.stream().map(returnItem -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", returnItem.getId());
            data.put("returnDate", returnItem.getReturnDate());
            data.put("quantity", returnItem.getQuantity());
            data.put("unitPrice", returnItem.getUnitPrice());
            data.put("totalAmount", returnItem.getTotalAmount());
            data.put("reason", returnItem.getReason());
            data.put("notes", returnItem.getNotes());
            
            if (returnItem.getProduct() != null) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("id", returnItem.getProduct().getId());
                productData.put("productName", returnItem.getProduct().getProductName());
                data.put("product", productData);
            }
            
            return data;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(returnsData);
    }

    @PostMapping("/api/returns")
    @ResponseBody
    public ResponseEntity<?> addReturn(@RequestBody Map<String, Object> returnData) {
        try {
            Return returnItem = new Return();
            
            Long productId = Long.valueOf(returnData.get("productId").toString());
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            returnItem.setProduct(product);
            returnItem.setReturnDate(java.time.LocalDate.parse((String) returnData.get("returnDate")));
            returnItem.setQuantity(Integer.valueOf(returnData.get("quantity").toString()));
            returnItem.setUnitPrice(new java.math.BigDecimal(returnData.get("unitPrice").toString()));
            returnItem.setReason((String) returnData.get("reason"));
            returnItem.setNotes((String) returnData.get("notes"));
            
            // Increase stock when product is returned
            int quantity = returnItem.getQuantity();
            productService.updateStock(productId, quantity);
            
            Return saved = returnRepository.save(returnItem);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/api/returns/{id}")
    @ResponseBody
    public ResponseEntity<?> updateReturn(@PathVariable Long id, @RequestBody Map<String, Object> returnData) {
        try {
            Return returnItem = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found"));
            
            // Revert previous stock change
            int oldQuantity = returnItem.getQuantity();
            Long oldProductId = returnItem.getProduct().getId();
            productService.updateStock(oldProductId, -oldQuantity);
            
            // Apply new values
            Long productId = Long.valueOf(returnData.get("productId").toString());
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            returnItem.setProduct(product);
            returnItem.setReturnDate(java.time.LocalDate.parse((String) returnData.get("returnDate")));
            returnItem.setQuantity(Integer.valueOf(returnData.get("quantity").toString()));
            returnItem.setUnitPrice(new java.math.BigDecimal(returnData.get("unitPrice").toString()));
            returnItem.setReason((String) returnData.get("reason"));
            returnItem.setNotes((String) returnData.get("notes"));
            
            // Apply new stock change
            int newQuantity = returnItem.getQuantity();
            productService.updateStock(productId, newQuantity);
            
            Return updated = returnRepository.save(returnItem);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/returns/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteReturn(@PathVariable Long id) {
        try {
            Return returnItem = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found"));
            
            // Revert stock change when deleting return
            Long productId = returnItem.getProduct().getId();
            int quantity = returnItem.getQuantity();
            productService.updateStock(productId, -quantity);
            
            returnRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

