package com.example.ecomweb.controller;

import com.example.ecomweb.entity.Product;
import com.example.ecomweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "Only image files are allowed");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("error", "File size must be less than 5MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("message", "Image uploaded successfully");
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("contentType", contentType);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long productId) {
        try {
            Optional<Product> productOpt = productService.getProductById(productId);
            
            if (!productOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Product product = productOpt.get();
            byte[] imageData = product.getImageData();
            
            if (imageData == null || imageData.length == 0) {
                return ResponseEntity.notFound().build();
            }
            
            HttpHeaders headers = new HttpHeaders();
            String contentType = product.getImageType();
            if (contentType != null) {
                headers.setContentType(MediaType.parseMediaType(contentType));
            } else {
                headers.setContentType(MediaType.IMAGE_JPEG);
            }
            
            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Map<String, String>> deleteProductImage(@PathVariable Long productId) {
        Map<String, String> response = new HashMap<>();
        
        try {
            Optional<Product> productOpt = productService.getProductById(productId);
            
            if (!productOpt.isPresent()) {
                response.put("error", "Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Product product = productOpt.get();
            product.setImageData(null);
            product.setImageName(null);
            product.setImageType(null);
            
            productService.updateProduct(product);
            
            response.put("message", "Product image deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to delete image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}