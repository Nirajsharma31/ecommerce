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
    
    @PostMapping("/simple-register")
    public ResponseEntity<Map<String, Object>> simpleRegister(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("Simple registration attempt with data: " + requestData);
            
            // Create user manually without validation
            User user = new User();
            user.setUsername(requestData.get("username"));
            user.setEmail(requestData.get("email"));
            user.setPassword(requestData.get("password"));
            user.setFullName(requestData.get("fullName"));
            user.setAddress(requestData.get("address"));
            user.setPhoneNumber(requestData.get("phoneNumber"));
            
            System.out.println("Created user object: " + user.getUsername());
            
            User savedUser = userService.registerUser(user);
            response.put("message", "User registered successfully via simple endpoint");
            response.put("user", Map.of(
                "id", savedUser.getId(),
                "username", savedUser.getUsername(),
                "email", savedUser.getEmail(),
                "fullName", savedUser.getFullName(),
                "role", savedUser.getRole()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Simple registration error: " + e.getMessage());
            e.printStackTrace();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("=== REGISTRATION ATTEMPT ===");
            System.out.println("Received data: " + requestData);
            
            // Manual validation with clear error messages
            String username = requestData.get("username");
            String email = requestData.get("email");
            String password = requestData.get("password");
            String fullName = requestData.get("fullName");
            
            // Basic validation
            if (username == null || username.trim().length() < 2) {
                response.put("error", "Username must be at least 2 characters");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (email == null || !email.contains("@")) {
                response.put("error", "Please enter a valid email address");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.length() < 3) {
                response.put("error", "Password must be at least 3 characters");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (fullName == null || fullName.trim().length() < 2) {
                response.put("error", "Full name must be at least 2 characters");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create user object
            User user = new User();
            user.setUsername(username.trim());
            user.setEmail(email.trim());
            user.setPassword(password);
            user.setFullName(fullName.trim());
            user.setAddress(requestData.get("address"));
            user.setPhoneNumber(requestData.get("phoneNumber"));
            
            System.out.println("Creating user: " + user.getUsername());
            
            User savedUser = userService.registerUser(user);
            
            System.out.println("User created successfully with ID: " + savedUser.getId());
            
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
        
        // Debug logging
        System.out.println("Login attempt for username: " + username);
        
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            if (!userOpt.isPresent()) {
                System.out.println("User not found: " + username);
                response.put("error", "Invalid username or password");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = userOpt.get();
            System.out.println("User found: " + user.getUsername() + ", Role: " + user.getRole());
            
            if (userService.authenticateUser(username, password)) {
                System.out.println("Authentication successful for: " + username);
                response.put("message", "Login successful");
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "address", user.getAddress() != null ? user.getAddress() : "",
                    "phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                    "role", user.getRole()
                ));
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Authentication failed for: " + username);
                response.put("error", "Invalid username or password");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
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