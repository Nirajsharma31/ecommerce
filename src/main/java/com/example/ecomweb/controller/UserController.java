package com.example.ecomweb.controller;

import com.example.ecomweb.entity.User;
import com.example.ecomweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/test-register")
    public ResponseEntity<Map<String, Object>> testRegister(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", requestData);
        response.put("status", "Data received successfully");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Debug logging
            System.out.println("Registration attempt for user: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Full Name: " + user.getFullName());
            
            User savedUser = userService.registerUser(user);
            response.put("message", "User registered successfully");
            response.put("user", Map.of(
                "id", savedUser.getId(),
                "username", savedUser.getUsername(),
                "email", savedUser.getEmail(),
                "fullName", savedUser.getFullName(),
                "role", savedUser.getRole()
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            System.out.println("Unexpected registration error: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        if (userService.authenticateUser(username, password)) {
            Optional<User> user = userService.findByUsername(username);
            if (user.isPresent()) {
                response.put("message", "Login successful");
                response.put("user", Map.of(
                    "id", user.get().getId(),
                    "username", user.get().getUsername(),
                    "email", user.get().getEmail(),
                    "fullName", user.get().getFullName(),
                    "address", user.get().getAddress() != null ? user.get().getAddress() : "",
                    "phoneNumber", user.get().getPhoneNumber() != null ? user.get().getPhoneNumber() : "",
                    "role", user.get().getRole()
                ));
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("error", "Invalid username or password");
        return ResponseEntity.badRequest().body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> existingUser = userService.findById(id);
            if (!existingUser.isPresent()) {
                response.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }
            
            user.setId(id);
            // Don't update password through this endpoint
            user.setPassword(existingUser.get().getPassword());
            User updatedUser = userService.updateUser(user);
            
            response.put("message", "User updated successfully");
            response.put("user", Map.of(
                "id", updatedUser.getId(),
                "username", updatedUser.getUsername(),
                "email", updatedUser.getEmail(),
                "fullName", updatedUser.getFullName(),
                "address", updatedUser.getAddress() != null ? updatedUser.getAddress() : "",
                "phoneNumber", updatedUser.getPhoneNumber() != null ? updatedUser.getPhoneNumber() : "",
                "role", updatedUser.getRole()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to update user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}