package com.example.ecomweb.service;

import com.example.ecomweb.entity.User;
import com.example.ecomweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean authenticateUser(String username, String password) {
        Optional<User> user = findByUsername(username);
        if (!user.isPresent()) {
            System.out.println("UserService: User not found for username: " + username);
            return false;
        }
        
        User foundUser = user.get();
        System.out.println("UserService: Found user: " + foundUser.getUsername());
        System.out.println("UserService: Stored password hash: " + foundUser.getPassword().substring(0, 10) + "...");
        System.out.println("UserService: Input password: " + password);
        
        boolean matches = passwordEncoder.matches(password, foundUser.getPassword());
        System.out.println("UserService: Password matches: " + matches);
        
        return matches;
    }
    
    public boolean isAdmin(Long userId) {
        Optional<User> user = findById(userId);
        return user.isPresent() && user.get().getRole() == User.Role.ADMIN;
    }
    
    public long getTotalUsersCount() {
        return userRepository.count();
    }
}