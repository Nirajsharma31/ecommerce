package com.example.ecomweb.controller;

import com.example.ecomweb.service.UserService;
import com.example.ecomweb.service.ProductService;
import com.example.ecomweb.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get total users count
            long totalUsers = userService.getTotalUsersCount();
            stats.put("totalUsers", totalUsers);
            
            // Get total products count
            long totalProducts = productService.getTotalProductsCount();
            stats.put("totalProducts", totalProducts);
            
            // Get total orders count
            long totalOrders = orderService.getTotalOrdersCount();
            stats.put("totalOrders", totalOrders);
            
            // Get total revenue
            BigDecimal totalRevenue = orderService.getTotalRevenue();
            stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            
            // Get pending orders count
            long pendingOrders = orderService.getPendingOrdersCount();
            stats.put("pendingOrders", pendingOrders);
            
            // Get low stock products count (products with stock <= 5)
            long lowStockProducts = productService.getLowStockProductsCount();
            stats.put("lowStockProducts", lowStockProducts);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Return default values in case of error
            stats.put("totalUsers", 0);
            stats.put("totalProducts", 0);
            stats.put("totalOrders", 0);
            stats.put("totalRevenue", BigDecimal.ZERO);
            stats.put("pendingOrders", 0);
            stats.put("lowStockProducts", 0);
            
            return ResponseEntity.ok(stats);
        }
    }
}