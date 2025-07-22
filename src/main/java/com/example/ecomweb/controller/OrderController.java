package com.example.ecomweb.controller;

import com.example.ecomweb.entity.Order;
import com.example.ecomweb.entity.User;
import com.example.ecomweb.service.OrderService;
import com.example.ecomweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String shippingAddress = request.get("shippingAddress").toString();
            String paymentMethod = request.get("paymentMethod").toString();
            
            Optional<User> user = userService.findById(userId);
            if (!user.isPresent()) {
                response.put("error", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Order order = orderService.createOrder(user.get(), shippingAddress, paymentMethod);
            response.put("message", "Order created successfully");
            response.put("orderId", order.getId());
            response.put("totalAmount", order.getTotalAmount());
            response.put("status", order.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        Optional<User> user = userService.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Order> orders = orderService.getUserOrders(user.get());
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return order.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/admin/status/{orderId}")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String status = request.get("status");
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            
            Order updatedOrder = orderService.updateOrderStatus(orderId, orderStatus);
            response.put("message", "Order status updated successfully");
            response.put("orderId", updatedOrder.getId());
            response.put("status", updatedOrder.getStatus());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "Invalid order status");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orderService.getAllOrders().size());
        stats.put("pendingOrders", orderService.getPendingOrdersCount());
        stats.put("confirmedOrders", orderService.getOrdersByStatus(Order.OrderStatus.CONFIRMED).size());
        stats.put("shippedOrders", orderService.getOrdersByStatus(Order.OrderStatus.SHIPPED).size());
        stats.put("deliveredOrders", orderService.getOrdersByStatus(Order.OrderStatus.DELIVERED).size());
        stats.put("cancelledOrders", orderService.getOrdersByStatus(Order.OrderStatus.CANCELLED).size());
        
        return ResponseEntity.ok(stats);
    }
}