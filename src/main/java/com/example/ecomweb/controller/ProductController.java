package com.example.ecomweb.controller;

import com.example.ecomweb.entity.Product;
import com.example.ecomweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product savedProduct = productService.saveProduct(product);
            response.put("message", "Product created successfully");
            response.put("product", savedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to create product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/with-image")
    public ResponseEntity<Map<String, Object>> createProductWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("category") String category,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Map<String, Object> response = new HashMap<>();
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuantity(stockQuantity);
            product.setCategory(category);
            product.setBrand(brand);

            if (image != null && !image.isEmpty()) {
                if (!image.getContentType().startsWith("image/")) {
                    response.put("error", "Only image files are allowed");
                    return ResponseEntity.badRequest().body(response);
                }
                if (image.getSize() > 5 * 1024 * 1024) {
                    response.put("error", "File size must be less than 5MB");
                    return ResponseEntity.badRequest().body(response);
                }

                byte[] imageBytes = image.getBytes();
                if (imageBytes.length > 10 * 1024 * 1024) {
                    response.put("error", "Image is too large for database storage");
                    return ResponseEntity.badRequest().body(response);
                }

                product.setImageData(imageBytes);
                product.setImageName(image.getOriginalFilename());
                product.setImageType(image.getContentType());
            }

            Product savedProduct = productService.saveProduct(product);
            response.put("message", "Product created successfully");
            response.put("product", savedProduct);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Failed to process image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Failed to create product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Product> existingProduct = productService.getProductById(id);
            if (!existingProduct.isPresent()) {
                response.put("error", "Product not found");
                return ResponseEntity.notFound().build();
            }
            
            product.setId(id);
            Product updatedProduct = productService.updateProduct(product);
            response.put("message", "Product updated successfully");
            response.put("product", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to update product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Product> product = productService.getProductById(id);
            if (!product.isPresent()) {
                response.put("error", "Product not found");
                return ResponseEntity.notFound().build();
            }
            
            productService.deleteProduct(id);
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to delete product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}