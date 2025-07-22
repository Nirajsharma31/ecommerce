package com.example.ecomweb.controller;

import com.example.ecomweb.entity.CartItem;
import com.example.ecomweb.entity.User;
import com.example.ecomweb.service.CartService;
import com.example.ecomweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            Optional<User> user = userService.findById(userId);
            if (!user.isPresent()) {
                response.put("error", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            CartItem cartItem = cartService.addToCart(user.get(), productId, quantity);
            response.put("message", "Product added to cart successfully");
            response.put("cartItem", cartItem);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId) {
        Optional<User> user = userService.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<CartItem> cartItems = cartService.getCartItems(user.get());
        return ResponseEntity.ok(cartItems);
    }
    
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @PathVariable Long cartItemId, 
            @RequestBody Map<String, Integer> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer quantity = request.get("quantity");
            CartItem updatedItem = cartService.updateCartItem(cartItemId, quantity);
            
            if (updatedItem == null) {
                response.put("message", "Cart item removed (quantity was 0)");
            } else {
                response.put("message", "Cart item updated successfully");
                response.put("cartItem", updatedItem);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Map<String, String>> removeFromCart(@PathVariable Long cartItemId) {
        Map<String, String> response = new HashMap<>();
        try {
            cartService.removeFromCart(cartItemId);
            response.put("message", "Item removed from cart successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/total/{userId}")
    public ResponseEntity<Map<String, Object>> getCartTotal(@PathVariable Long userId) {
        Optional<User> user = userService.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        BigDecimal total = cartService.getCartTotal(user.get());
        long itemCount = cartService.getCartItemCount(user.get());
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("itemCount", itemCount);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<User> user = userService.findById(userId);
            if (!user.isPresent()) {
                response.put("error", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            cartService.clearCart(user.get());
            response.put("message", "Cart cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}