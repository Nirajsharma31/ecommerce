package com.example.ecomweb.controller;

import com.example.ecomweb.entity.Product;
import com.example.ecomweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping("/simple-product")
    public ResponseEntity<Map<String, Object>> createSimpleProduct() {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = new Product();
            product.setName("Test Product");
            product.setDescription("Test Description");
            product.setPrice(new BigDecimal("99.99"));
            product.setStockQuantity(10);
            product.setCategory("Electronics");
            product.setBrand("Test Brand");
            
            Product savedProduct = productService.saveProduct(product);
            response.put("message", "Simple product created successfully");
            response.put("product", savedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to create simple product: " + e.getMessage());
            response.put("stackTrace", e.getStackTrace());
            e.printStackTrace(); // This will print to console
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/product-with-image-debug")
    public ResponseEntity<Map<String, Object>> createProductWithImageDebug(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String priceStr,
            @RequestParam("stockQuantity") String stockStr,
            @RequestParam("category") String category,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // Debug information
            response.put("debug_name", name);
            response.put("debug_description", description);
            response.put("debug_price", priceStr);
            response.put("debug_stock", stockStr);
            response.put("debug_category", category);
            response.put("debug_brand", brand);
            
            if (image != null) {
                response.put("debug_image_name", image.getOriginalFilename());
                response.put("debug_image_size", image.getSize());
                response.put("debug_image_type", image.getContentType());
            } else {
                response.put("debug_image", "No image provided");
            }
            
            // Try to parse price and stock
            BigDecimal price;
            Integer stockQuantity;
            
            try {
                price = new BigDecimal(priceStr);
                response.put("debug_price_parsed", price);
            } catch (Exception e) {
                response.put("error", "Invalid price format: " + priceStr);
                return ResponseEntity.badRequest().body(response);
            }
            
            try {
                stockQuantity = Integer.parseInt(stockStr);
                response.put("debug_stock_parsed", stockQuantity);
            } catch (Exception e) {
                response.put("error", "Invalid stock quantity format: " + stockStr);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create product
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuantity(stockQuantity);
            product.setCategory(category);
            product.setBrand(brand);
            
            // Handle image if provided
            if (image != null && !image.isEmpty()) {
                try {
                    byte[] imageBytes = image.getBytes();
                    product.setImageData(imageBytes);
                    product.setImageName(image.getOriginalFilename());
                    product.setImageType(image.getContentType());
                    response.put("debug_image_processed", "Image data set successfully");
                } catch (Exception e) {
                    response.put("error", "Failed to process image: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            // Try to save
            Product savedProduct = productService.saveProduct(product);
            response.put("message", "Product created successfully with debug info");
            response.put("product_id", savedProduct.getId());
            response.put("product_name", savedProduct.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Failed to create product: " + e.getMessage());
            response.put("exception_class", e.getClass().getSimpleName());
            
            // Get the root cause
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            response.put("root_cause", cause.getMessage());
            response.put("root_cause_class", cause.getClass().getSimpleName());
            
            e.printStackTrace(); // Print full stack trace to console
            return ResponseEntity.badRequest().body(response);
        }
    }
}